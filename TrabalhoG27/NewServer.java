package SD.TrabalhoG27;

import sd23.JobFunction;
import sd23.JobFunctionException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


public class NewServer {
    final static int WORKERS_PER_CONNECTION = 3;

    private static final Accounts accounts = new Accounts();
    private static final ReentrantLock lock = new ReentrantLock();

    private static final Statistics stats = new Statistics(10000);

    private static WorkTeam wt = new WorkTeam();
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
                            frame = new Frame(111,0,null);
                            System.out.println("worker ativo");
                            Thread.sleep(2000000);
                        }

                        //unir o server aos workers
                        if(frame.tag == 254){
                            isWorker = true;
                            //adicionar o worker à lista de workers
                            wt.setNewWorker(c);
                            System.out.println("worker adicionado");
                        }
                        else if(frame.tag == 253){
                            //remover o worker da lista de workers
                            wt.removeWorker(c);
                            System.out.println("worker removido");
                        }
                        //fazer logout
                        else if (frame.tag == 0){
                            System.out.println("Got logout attempt");
                            accounts.setActive(((Account)frame.obj).getName(), false);
                        }
                        //fazer login
                        else if (frame.tag == 1){
                            System.out.println("Got login attempt");

                            lock.lock();
                            c.send(1, frame.ask, accounts.loginAttempt((Account)frame.obj));
                            lock.unlock();
                        }
                        else if (frame.tag == 2){
                            Quest q = (Quest)frame.obj;
                            stats.newTask(q.getMemory());
                            //inicia uma thread para resposta
                            Runnable response = () -> {
                                //atribui a um worker disponivel o pedido
                                TaggedConnection worker = wt.getWorker();
                                try {
                                    worker.send(q.getCode());

                                    //recebe do worker o resultado
                                    String result = worker.receiveResult();

                                    //liberta o worker
                                    wt.makeAvailable(worker);
                                    //envia para o cliente o resultado
                                    System.out.println(result);
                                    System.out.println("got here");

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

                        //devolver as estatisticas
                        else if (frame.tag == 3){
                            c.send(3,frame.ask, stats.getAvailableMemory(), stats.getActiveTasks());
                        }
                        else {
                            System.out.println("not implemented yet server-ln59 ");
                        }
                    }
                } catch (Exception ignored) { }
            };
            new Thread(manager).start();
        }

    }
}
