package SD.TrabalhoG27;


import java.io.IOException;

public class ClientControler {
    private final Menu menu;
    private final TaggedConnection c;
    int ask;


    public ClientControler(TaggedConnection c) {
        this.menu = new Menu();
        this.c = c;
        ask = 0;
    }

    public Account login() throws IOException {

        Account acc;
        ask+=1;
        while ((menu.mainMenu())!= 0) {
            acc = menu.RegisterMenu();
            c.send(1,1,ask, acc);
            Frame frame = c.receive();
            if (frame.tag == 1 && frame.src == 0){
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
    public void askQuery() {

        int i;
        while ((i = menu.clientMenu()) != 0) {
            ask += 1;
            //askquest
            if (i == 1) {
                Quest quest = new Quest(1000, menu.askQuest());
                Thread thread = new Thread (() -> {
                    try {
                        c.send(2, 1, ask, quest);
                        Frame frame = c.receive();
                        if (frame.tag == 2 && frame.src == 0) {
                            System.out.println("quest number: "+ frame.ask +" content:" + frame.obj);//o que querem daqui
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();
            }
            //askstats
            else if (i == 2){
                Thread thread = new Thread (() -> {
                try {
                    c.send(3, 1, ask, "");
                    Frame frame = c.receive();
                    if (frame.tag == 3 && frame.src == 0) {
                        System.out.println("quest number: "+ frame.ask +" content:" + frame.obj);
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                });
                thread.start();
            }
        }
    }
    public void logout(Account acc) throws IOException {
            if(acc != null)
                c.send(0,1,ask,acc);
    }
}
