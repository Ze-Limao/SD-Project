package SD.TrabalhoG27;


import java.io.IOException;

public class ClientControler {
    private final Menu menu;
    private final Demultiplexer m;
    int ask;


    public ClientControler(Demultiplexer m) {
        this.menu = new Menu();
        this.m = m;
        ask = 0;
    }

    public Account login() throws Exception {

        Account acc;
        ask+=1;
        while ((menu.mainMenu())!= 0) {
            acc = menu.RegisterMenu();
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
    public void askQuery(){

        int i;
        while ((i = menu.clientMenu()) != 0) {
            ask += 1;
            //askquest
            if (i == 1) {
                Quest quest = new Quest(1000, menu.askQuest());
                Thread thread = new Thread (() -> {
                    try {
                        m.send(2, ask, quest);
                        Frame frame = m.receive(2);
                        if (frame.tag == 2) {
                            System.out.println("quest number: "+ frame.ask +" content:" + frame.obj);//o que querem daqui
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();
            }
            //askstats
            else if (i == 2){
                Thread thread = new Thread (() -> {
                    try {
                        m.send(3, ask, "");
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
    public void logout(Account acc) throws IOException {
        if(acc != null)
            m.send(0,ask,acc);
    }
}
