package SD.TrabalhoG27;


import java.io.IOException;
import java.net.Socket;

/**
 * tag1 -> enviar um nome e palavra passe
 *              tag + length+nome+legth+passe
 *         server deverá fazer lock desse client caso seja aceite
 *      <- receber luz verde para pedir cenas ou dizer que as credenciais são invalidas
 *              tag + boolean
 * <p>
 * tag2 -> enviar uma tarefa para ser executada
 *              tag + codigo + memoria necessaria
 *         server deverá atualizar a memoria disponivel e aumentar o valor das tarefas em espera, diminuir no final
 *      <- receber o resultado ou numero e mensagem de erro
 *              tag + boolean + resultado/(numero + length + mensagem)
 * <p>
 * tag3 -> pedir o estado atual
 *              tag
 *      <- devolver a memoria disponivel e o numero de tarefas pendentes
 *          tag + int + int
 * */

public class Client {
    public static void main(String[] args) throws IOException {
        //make connection
        Socket s = new Socket("localhost", 12345);
        TaggedConnection c = new TaggedConnection(s);
        ClientControler cc = new ClientControler(c);

        //get client list
        while(true) {
            Account acc = cc.login();
            if (acc == null) {
                return;
            }
            while (cc.askQuery(acc)) {

            }
            cc.logout(acc);
        }

        //send a mensage with tag
        //autenticate
        //execute queries using that cient
    }
}

