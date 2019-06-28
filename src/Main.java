
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Erro.O argumento não foi passado.");
            System.exit(-1);
        }
        Parser parser = Parser.getInstanciaParser(args[0]);
        parser.programa();
    }
}
