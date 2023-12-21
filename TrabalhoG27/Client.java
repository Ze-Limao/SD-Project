package SD.TrabalhoG27;


import java.io.IOException;
import java.net.Socket;

/**
 * src = 0 <- server
 * src = 1 -> PC
 * tag0 -> logout
 *
 * tag1 -> enviar um nome e palavra passe
 *              tag + src + length+nome+legth+passe
 *         server deverá fazer lock desse client caso seja aceite
 *      <- receber luz verde para pedir cenas ou dizer que as credenciais são invalidas
 *              tag + src + boolean
 * <p>
 * tag2 -> enviar uma tarefa para ser executada
 *              tag + src + codigo + memoria necessaria
 *         server deverá atualizar a memoria disponivel e aumentar o valor das tarefas em espera, diminuir no final
 *      <- receber o resultado ou numero e mensagem de erro
 *              tag + src + boolean + resultado/(numero + length + mensagem)
 * <p>
 * tag3 -> pedir o estado atual
 *              tag + src
 *      <- devolver a memoria disponivel e o numero de tarefas pendentes
 *          tag + src + int + int
 * */

public class Client {
    public static void main(String[] args) throws IOException {
        //make connection
        Socket s = new Socket("localhost", 12345);
        TaggedConnection c = new TaggedConnection(s);
        ClientControler cc = new ClientControler(c);
        Account acc = null;
        try {
            //get client list
            while (true) {
                acc = cc.login();
                if (acc == null) {
                    return;
                }
                cc.askQuery();
                cc.logout(acc);
            }
        } finally {
            cc.logout(acc);
        }
        //send a mensage with tag
        //autenticate
        //execute queries using that cient
    }
}
