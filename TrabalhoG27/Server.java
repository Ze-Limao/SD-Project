package SD.TrabalhoG27;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import sd23.*;


public class Server {
    final static int WORKERS_PER_CONNECTION = 10;

    private static final Accounts accounts = new Accounts();
    private static final ReentrantLock lock = new ReentrantLock();

    private static final Statistics stats = new Statistics(10000);

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);

        while(true) {
            Socket s = ss.accept();
            TaggedConnection c = new TaggedConnection(s);
            Runnable worker = () -> {
                try (c) {

                    for (;;) {
                        Frame frame = c.receiveFromPC();
                        if (frame.src != 1){
                            System.out.println("received something from src:" + frame.src);
                            return;
                        }
                        if (frame.tag == 0){
                            System.out.println("Got logout attempt");
                            accounts.setActive(((Account)frame.obj).getName(), false);
                        }

                        else if (frame.tag == 1){
                            System.out.println("Got login attempt");

                            lock.lock();
                            c.send(1,0, frame.ask, accounts.loginAttempt((Account)frame.obj));
                            lock.unlock();
                        }

                        else if (frame.tag == 2){
                            Quest q = (Quest)frame.obj;
                            stats.newTask(q.getMemory());
                            try {
                                // obter a tarefa de ficheiro, socket, etc...
                                byte[] job = q.getCode().getBytes();
                                // executar a tarefa
                                byte[] output = JobFunction.execute(job);
                                stats.endTask(q.getMemory());
                                // utilizar o resultado ou reportar o erro
                                System.err.println("success, returned "+output.length+" bytes");
                                c.send(2,0, frame.ask, true,output);
                            } catch (JobFunctionException e) {
                                System.err.println("job failed: code=" + e.getCode() + " message=" + e.getMessage());
                                c.send(2,0,frame.ask, false,e.getCode(),e.getMessage());
                            }
                        }
                        else if (frame.tag == 3){
                            c.send(3,0,frame.ask, stats.getAvailableMemory(), stats.getActiveTasks());
                        }
                        else {
                            System.out.println("not implemented yet server-ln59 ");
                        }
                    }

                } catch (Exception ignored) { }
            };

            for (int i = 0; i < WORKERS_PER_CONNECTION; ++i)
                new Thread(worker).start();
        }

    }
}
