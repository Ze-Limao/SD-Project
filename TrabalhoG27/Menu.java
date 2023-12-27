package SD.TrabalhoG27;

import java.rmi.ServerError;
import java.util.InputMismatchException;
import java.io.*;
import java.util.*;
import java.util.zip.ZipError;

import static java.lang.Math.random;

public class Menu  {
    private static final List<String> tentativaMensagens = new ArrayList<>();

    private static void addCenas() {
        tentativaMensagens.add("\u001B[35m" + "Tenta novamente." + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Há terceira é de vez." + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Ou talvez não..." + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Tenta outra vez." + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "És estúpido?" + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Mas estamos a brincar? O meu cão não precisa de 7 vezes para introduzir um um filepath!" + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Tutorial como ir buscar um filepath: \n1.Escrever um caminho que exista. \n2.Se estás a ler este passo podes te candidatar ao McDonalds." + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Nem o McDonalds te aceitaria man" + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Última tentativa, que eu já não te posso ver mais à frente, anda lá, oupa." + "\u001B[0m");
    }

    public static int mainMenu() {
        int option = -1;
        Scanner input = new Scanner(System.in);

        while (option != 0 && option != 1) {
            try {
                System.out.println("""
                        \tWelcome To Our Project

                        1 -> Login
                        0 -> Exit
                        Choose wisely:""");
                option = input.nextInt();
            } catch (InputMismatchException e) {
                // Limpar o buffer do scanner
                input.nextLine();
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        return option;
    }

    public static Account RegisterMenu() {

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

    public static int clientMenu() {
        int option = -1;
        Scanner input = new Scanner(System.in);

        while (option != 1 && option != 0 && option != 2) {
            System.out.println("""
                    \t\tWelcome
                        
                    1 -> Make a Quest
                    2 -> Get Server statistics
                    0 -> Logout""");

            try {
                // Verifica se o próximo token é um número
                if (input.hasNextInt()) {
                    option = input.nextInt();
                } else {
                    // Limpa o buffer do scanner em caso de entrada inválida
                    input.next();
                    System.out.println("Invalid input. Please enter a number.");
                }
            } catch (InputMismatchException e) {
                // Limpa o buffer do scanner em caso de exceção de tipo de entrada
                input.next();
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return option;
    }

    public static String askFilepath() throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("filepath:"); //
        return input.nextLine();
    }

    public static String askQuest() throws IOException{
        StringBuilder quest = new StringBuilder();
        String filepath = askFilepath();
        File file = new File(filepath);
        addCenas();
        int n = 0;
        while (!file.exists()) {
            if(n == 9){
                System.err.println("DESISTO.");
                System.exit(0);
            }
            System.out.println(tentativaMensagens.get(n++));
            filepath = askFilepath();
            file = new File(filepath);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                quest.append(line).append("\n");
            }
        }
        return quest.toString();
    }

    public static void saveResultsInFile(String result) throws IOException {
        String filepath = "tests/results";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, true))) {
            writer.write(result + "\n\n");
        }
    }

}
