package SD.TrabalhoG27;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class Server {
    private static final Accounts accounts = new Accounts();
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition cond = lock.newCondition();


    private static final Statistics stats = new Statistics(2000);

    private static final WorkTeam wt = new WorkTeam();


    public static boolean serverSwitcher(Frame frame, TaggedConnection c) throws IOException {
        if (frame == null)
            return true;
        switch(frame.tag){
            case 254 -> { // adicionar worker
                //adicionar o worker à lista de workers
                stats.updateMemory(true, frame.ask);
                wt.setNewWorker(c);
                System.out.println("worker adicionado");
                return true;
            }
            case 253 -> { // remover worker
                stats.updateMemory(false, frame.ask);
                wt.removeWorker(c);
                System.out.println("worker removido");
            }
            case 0 -> {
                System.out.println("Got logout attempt");
                accounts.setActive(((Account)frame.obj).getName(), false);
            }
            case 1 ->{
                System.out.println("Got login attempt");
                lock.lock();
                c.send(1, frame.ask, accounts.loginAttempt((Account)frame.obj));
                lock.unlock();
            }
            case 2 -> {
                Quest q = (Quest)frame.obj;
                //inicia uma thread para resposta
                Runnable response = () -> {
                    stats.newTask(q.getMemory());
                    //atribui a um worker disponivel o pedido
                    TaggedConnection worker;
                    try {
                        worker = wt.getWorker();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        worker.send(q.getCode());

                        //recebe do worker o resultado
                        String result = worker.receiveResult();

                        //liberta o worker
                        wt.makeAvailable(worker);

                        //envia para o cliente o resultado
                        c.send(2,frame.ask,result);
                        stats.endTask(q.getMemory());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //entrega o resultado

                };
                Thread t = new Thread(response);
                t.start();
            }
            case 3 -> {
                System.out.println("received a stats quest");
                c.send(3,frame.ask, stats.getAvailableMemory(), stats.getActiveTasks());
            }
            default -> System.out.println("not implemented yet server-ln59 ");
        }
        return false;

    }

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);

        while(true) {
            Socket s = ss.accept();
            TaggedConnection c = new TaggedConnection(s);
            Runnable manager = () -> {
                boolean isWorker = false;
                try (c) {
                    for (;;) {
                        Frame frame;
                        if (!isWorker){
                            frame = c.receiveFromClient();
                        } else {
                            frame = null;
                            System.out.println("worker ativo");
                            lock.lock();
                            cond.await();
                            lock.unlock();
                        }
                        isWorker = serverSwitcher(frame, c);
                    }
                } catch (Exception ignored) { }
            };
            new Thread(manager).start();
        }

    }
}
