package TrabalhoG27;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    final static int WORKERS_PER_CONNECTION = 3;

    private static Map<String, Account> accounts = new HashMap<>();

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);

        while(true) {
            Socket s = ss.accept();
            TaggedConnection c = new TaggedConnection(s);

            Runnable worker = () -> {
                try (c) {
                    for (;;) {
                        Frame frame = c.receive();

                        if (frame.tag == 0 && frame.src == 1){
                            System.out.println("Got logout attempt");

                            Account acc = (Account)frame.obj;
                            lock.lock();
                            accounts.get(acc.getName()).setActive(false);
                            lock.unlock();
                        }
                        else if (frame.tag == 1 && frame.src == 1){
                            System.out.println("Got login attempt");

                            Account acc = (Account)frame.obj;
                            lock.lock();
                            if (!accounts.containsKey(acc.getName())) {
                                accounts.put(acc.getName(), new Account(acc));
                                c.send(1,0, true);

                            } else if (accounts.get(acc.getName()).verifyPassword(acc) && !accounts.get(acc.getName()).isActive()) {
                                accounts.get(acc.getName()).setActive(true);
                                c.send(1,0, true);

                            }
                            else {
                                c.send(1,0, false);

                            }
                            lock.unlock();
                        }
                        else {
                            System.out.println("not implemented yet server-ln59 ");
                        }
                        /**else if (frame.tag == 2){
                            System.out.println("new query\n");
                            String str = (String)frame.obj;
                            try {
                                // obter a tarefa de ficheiro, socket, etc...
                                byte[] job = str.getBytes();
                                // executar a tarefa
                                byte[] output = JobFunction.execute(job);

                                // utilizar o resultado ou reportar o erro
                                System.err.println("success, returned "+output.length+" bytes");

                            } catch (JobFunctionException e) {
                                System.err.println("job failed: code="+e.getCode()+" message="+e.getMessage());
                            }
                        }*/
                    }
                } catch (Exception ignored) { }
            };

            for (int i = 0; i < WORKERS_PER_CONNECTION; ++i)
                new Thread(worker).start();
        }

    }
}
