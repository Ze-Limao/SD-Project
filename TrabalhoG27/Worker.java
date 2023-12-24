package SD.TrabalhoG27;

import java.net.Socket;
import sd23.JobFunction;
import sd23.JobFunctionException;
public class Worker {
    public static void main(String[] args) throws Exception {
        //make connection
        Socket s = new Socket("localhost", 12345);
        TaggedConnection c = new TaggedConnection(s);
        try {
            //connect worker
            c.send(254);
            System.out.println("asked for connection");
            while (true) {
                String q = c.receiveQuest();

                try {
                    // obter a tarefa de ficheiro, socket, etc...
                    byte[] job = q.getBytes();
                    // executar a tarefa
                    byte[] output = JobFunction.execute(job);
                    // utilizar o resultado ou reportar o erro
                    System.err.println("success, returned "+output.length+" bytes");
                    c.send(true,output);
                } catch (JobFunctionException e) {
                   String aux = ("job failed: code=" + e.getCode() + " message=" + e.getMessage());
                    System.err.println(aux);
                   c.send(false,aux);
                }
            }
        } finally {
            //disconect Worker
            c.send(253);
        }
    }
}
