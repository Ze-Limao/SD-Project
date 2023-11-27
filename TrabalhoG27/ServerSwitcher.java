package SD.TrabalhoG27;

import sd23.JobFunction;
import sd23.JobFunctionException;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class ServerSwitcher {
    private final TaggedConnection c;
    private Accounts accounts;
    private final ReentrantLock lock;

    public ServerSwitcher(TaggedConnection c) {
        this.c = c;
        this.accounts = new Accounts();
        this.lock = new ReentrantLock();
    }

    public void execute() throws IOException {
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
                c.send(1,0, frame.ask, accounts.loginAttempt((Account)frame.obj));
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
                    c.send(2,0,frame.ask, true,output);
                } catch (JobFunctionException e) {
                    System.err.println("job failed: code="+e.getCode()+" message="+e.getMessage());
                    c.send(2,0,frame.ask, false,e.getCode(),e.getMessage());
                }
            }
            else {
                System.out.println("not implemented yet server-ln59 ");
            }
        }
    }

}
