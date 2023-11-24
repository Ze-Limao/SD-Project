package SD.TrabalhoG27;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import sd23.*;
public class Server {
    final static int WORKERS_PER_CONNECTION = 3;

    private static final Accounts accounts = new Accounts();
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);

        while(true) {
            Socket s = ss.accept();
            TaggedConnection c = new TaggedConnection(s);
            Runnable worker = () -> {
                try (c) {

                    for (;;) {
                        Frame frame = c.receive();
                        if (frame.src != 1){
                            System.out.println("recieved something from src:" + frame.src);
                            return;
                        }
                        if (frame.tag == 0){
                            System.out.println("Got logout attempt");

                            accounts.setActive(((Account)frame.obj).getName(), false);

                        }

                        else if (frame.tag == 1){
                            System.out.println("Got login attempt");

                            lock.lock();
                            c.send(1,0, accounts.loginAttempt((Account)frame.obj));
                            lock.unlock();
                        }

                        else if (frame.tag == 2){
                            Quest q = (Quest)frame.obj;
                            try {
                                // obter a tarefa de ficheiro, socket, etc...
                                byte[] job = q.getCode().getBytes();
                                // executar a tarefa
                                byte[] output = JobFunction.execute(job);

                                // utilizar o resultado ou reportar o erro
                                System.err.println("success, returned "+output.length+" bytes");
                                c.send(2,0,true,output);
                            } catch (JobFunctionException e) {
                                System.err.println("job failed: code="+e.getCode()+" message="+e.getMessage());
                                c.send(2,0,false,e.getCode(),e.getMessage());
                            }
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
