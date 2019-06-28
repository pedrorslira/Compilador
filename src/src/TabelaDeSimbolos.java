
import java.util.Stack;

public class TabelaDeSimbolos {

    private Stack<Token> tokens;

    public TabelaDeSimbolos() {
        this.tokens = new Stack<>();
    }

    public Stack getTokens() {
        return tokens;
    }

    public void setTokens(Stack tokens) {
        this.tokens = tokens;
    }

    public void add(Token token) {
        tokens.push(token);
    }

    public void desativarBloco() {
        int tam = tokens.size();
        for (int i = 0; i < tam; i++) {
            if (tokens.peek().getTipoToken() == TipoDeToken.AbreChaves) {
                tokens.pop();
                return;
            }
            tokens.pop();
        }
    }

    public TipoDeToken buscarTipoEscopo(Token token) {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).getTipoToken() == TipoDeToken.AbreChaves) {
                break;
            }
            if (tokens.get(i).getLexemaInicial().equals(token.getLexemaInicial())) {
                return tokens.get(i).getTipo();
            }
        }
        return null;
    }

    public TipoDeToken buscarTipo(Token token) {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).getLexemaInicial().equals(token.getLexemaInicial())) {
                return tokens.get(i).getTipo();
            }
        }
        return null;
    }
}
