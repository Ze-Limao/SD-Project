package SD.TrabalhoG27;


import java.io.IOException;

public class ClientControler {
    private final Menu menu;
    private final TaggedConnection c;

    public ClientControler(TaggedConnection c) {
        this.menu = new Menu();
        this.c = c;
    }

    public Account login() throws IOException {

        Account acc;
        while ((menu.mainMenu())!= 0) {
            acc = menu.RegisterMenu();

            c.send(1,1,acc);
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
    public boolean askQuery() throws IOException {

        int i;
        while ((i = menu.clientMenu()) != 0) {

            //askquest
            if (i == 1) {
                Quest quest = new Quest(1000, menu.askQuest());
                c.send(2, 1, quest);
                Frame frame = c.receive();
                if (frame.tag == 2 && frame.src == 0) {
                    System.out.println((String) frame.obj);//o que querem daqui
                }
            }
            //askstats
            if (i == 2){
                c.send(3, 1,"");
                Frame frame = c.receive();
                if (frame.tag == 3 && frame.src == 0) {
                    System.out.println((String) frame.obj);
                }
            }


        }
        return false;
    }
    public void logout(Account acc) throws IOException {
        c.send(0,1,acc);
    }
}
