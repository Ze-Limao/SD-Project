package SD.TrabalhoG27;

import java.util.InputMismatchException;
import java.io.*;
import java.util.*;

public class Menu  {
    private static final List<String> tentativaMensagens = new ArrayList<>();

    private static void addCenas() {
        tentativaMensagens.add("\u001B[35m" + "Tenta novamente." + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Há terceira é de vez." + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Ou talvez não..." + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Tenta outra vez." + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "És estúpido?" + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Mas estamos a brincar? O meu cão não precisa de 7 vezes para introduzir um um filepath!" + "\u001B[0m");
        tentativaMensagens.add("""
                \u001B[35mTutorial como ir buscar um filepath:\s
                1.Escrever um caminho que exista.\s
                2.Se estás a ler este passo podes te candidatar ao McDonalds.\u001B[0m""");
        tentativaMensagens.add("\u001B[35m" + "Nem o McDonalds te aceitaria man" + "\u001B[0m");
        tentativaMensagens.add("\u001B[35m" + "Última tentativa, que eu já não te posso ver mais à frente, anda lá, oupa." + "\u001B[0m");
    }

    public static int mainMenu() {
        int option = -1;
        Scanner input = new Scanner(System.in);

        while (option != 0 && option != 1) {
            try {
                System.out.println("""
                        \tBem vindo ao nosso Projeto

                        1 -> Iniciar Sessão
                        0 -> Sair
                        """);
                option = input.nextInt();
            } catch (InputMismatchException e) {
                // Limpar o buffer do scanner
                input.nextLine();
                System.out.println("Input invalido, por favor insira um número inteiro válido.");
            }
        }

        return option;
    }

    public static Account RegisterMenu() {

        System.out.println("\t\tRegiste-se");
        //password é escrita
        Scanner input = new Scanner(System.in);

        System.out.println("nome: ");
        String username = input.nextLine();

        System.out.println("Palavra-passe: ");
        String password = input.nextLine();

        return new Account(username, password);
    }

    public static int clientMenu() {
        int option = -1;
        Scanner input = new Scanner(System.in);

        while (option != 1 && option != 0 && option != 2) {
            System.out.println("""
                    \t\tbem vindo
                        
                    1 -> Fazer um pedido
                    2 -> pedir o estado ao servidor
                    0 -> sair""");

            try {
                // Verifica se o próximo token é um número
                if (input.hasNextInt()) {
                    option = input.nextInt();
                } else {
                    // Limpa o buffer do scanner em caso de entrada inválida
                    input.next();
                    System.out.println("Input invalido, por favor insira um número inteiro válido");
                }
            } catch (InputMismatchException e) {
                // Limpa o buffer do scanner em caso de exceção de tipo de entrada
                input.next();
                System.out.println("Input invalido, por favor insira um número inteiro válido");
            }
        }
        return option;
    }

    public static String askFilepath(){
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
