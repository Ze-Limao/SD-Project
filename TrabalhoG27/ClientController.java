package SD.TrabalhoG27;


import java.io.IOException;

public class ClientController {
    private final Demultiplexer m;
    int ask;


    public ClientController(Demultiplexer m) {
        this.m = m;
        ask = 0;
    }
    /**metodo com a execução do login*/
    public Account login() throws Exception {

        Account acc;
        ask+=1;
        while ((Menu.mainMenu())!= 0) {
            acc = Menu.RegisterMenu();
            m.send(1,ask, acc);
            Frame frame = m.receive(1);
            if (frame.tag == 1){
                Boolean loginSucceeded = (Boolean)frame.obj;
                if (loginSucceeded) {
                    System.out.println("login succeeded\n");
                    return acc;
                }
                else{
                    System.out.println("The credentials you inserted are not from a offline account\n");
                }
            }
        }
        return null;
    }
    /**metodo para processar uma querst*/
    public void askQuery() throws IOException{

        int i;
        while ((i = Menu.clientMenu()) != 0) {
            ask += 1;
            //ask quest
            if (i == 1) {
                Quest quest = new Quest(1000, Menu.askQuest());
                Thread thread = new Thread (() -> {
                    try {
                        m.send(2, ask, quest);
                        Frame frame = m.receive(2);
                        if (frame.tag == 2) {
                            String result = "quest number " + frame.ask + "\ncontent:" + frame.obj;
                            System.out.println(result);     //o que querem daqui
                            Menu.saveResultsInFile(result);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();
            }
            //ask stats
            else if (i == 2){
                Thread thread = new Thread (() -> {
                    try {
                        m.send(3, ask);
                        Frame frame = m.receive(3);
                        if (frame.tag == 3) {
                            System.out.println("quest number: "+ frame.ask +" content:" + frame.obj);
                        }
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();
            }
        }
    }
    /**metodo para fazer o logout da conta*/
    public void logout(Account acc) throws IOException {
        if(acc != null)
            m.send(0,ask,acc);
    }
}
