package SD.TrabalhoG27;

import java.util.Map;
import java.util.Scanner;

public class Menu  {

    public Menu (){
    }
    public int mainMenu(){
        int option =-1;
        Scanner input = new Scanner(System.in);

        while (option != 0 && option != 1) {

            System.out.println("""
                    \tWelcome To Our Project

                    1 -> Login
                    0 -> Exit
                    chose wisely:""");
            option = input.nextInt();
        }
        return option;
    }

    public Account RegisterMenu() {

        System.out.println("\t\tRegist Yourserlf");
        /**
//password não é escrita
         java.io.Console console = System.console();

         * if (console != null) {
         while (true) {
         String username = console.readLine("\nUsername: ");
         String password = new String(console.readPassword("Password: "));
         if (!contas.containsKey(username)) {
         contas.put(username, new Conta(username, password));
         return 4;
         }

         System.out.println("""
         Account already exists

         Do you want to:
         anything -> Try Again
         0    -> Go Back

         ->""");

         if (Integer.parseInt(console.readLine()) == 0)
         return 3;
         }
         }*/
        //password é escrita
        Scanner input = new Scanner(System.in);

        System.out.println("username: ");
        String username = input.nextLine();

        System.out.println("Password: ");
        String password = input.nextLine();

        return new Account(username, password);
    }

    public int clientMenu(){
        int option = -1;
        Scanner input = new Scanner(System.in);

        while (option != 1 && option != 0) {
            System.out.println( """
                    \t\tWelcome
                        
                    1 -> Make a Quest
                    0 -> Logout
                    
                    """);
            option = input.nextInt();
        }
        return option;

    }

    public String askQuest(){
        System.out.println("quest:");
        Scanner input = new Scanner(System.in);
            return input.nextLine();

    }

}
