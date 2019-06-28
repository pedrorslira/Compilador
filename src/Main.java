
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String path = "./src/codigo.txt";
        /*if (args.length == 0) {
            System.out.println("Erro.O argumento não foi passado.");
            System.exit(-1);
        }*/
        Parser parser = new Parser(path);
        parser.programa();
    }

}
