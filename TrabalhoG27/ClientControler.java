package TrabalhoG27;


import java.io.IOException;

public class ClientControler {
    private Menu menu;
    private TaggedConnection c;

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
    public boolean askQuery(Account acc) throws IOException {

        while (menu.clientMenu() != 0) {

            String quest = menu.askQuest();


            c.send(2,1,quest);
            Frame frame = c.receive();
            if (frame.tag == 2){
                System.out.println(frame.obj.toString());
            }

        }
        return false;
    }
    public void logout(Account acc) throws IOException {
        c.send(0,1,acc);
    }
}


/**
 * public void login() {
 *
 *         int chosenMenu = menu.mainMenu();
 *         while (chosenMenu != 0) {
 *             switch (chosenMenu) {
 *                 case 1:
 *                     chosenMenu = menu.RegisterMenu(accs);
 *
 *                     break;
 *                 case 2:
 *                     chosenMenu = menu.LoginMenu(accs);
 *                     break;
 *                 case 3:
 *                     chosenMenu = menu.mainMenu();
 *                     break;
 *                 case 4:
 *                     chosenMenu = menu.clientMenu();
 *                     break;
 *                 case 5:
 *                     String quest = menu.askQuest();
 *                     //executequest(quest);
 *                     System.out.println("execute query not implemented yet");
 *                     chosenMenu = 4;
 *                     break;
 *                 default:
 *                     return;
 *             }
 *         }
 *     }
 *     */