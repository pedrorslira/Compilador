
public class Token {

    private TipoDeToken tipo;
    private String lexema;
    private int linha;
    private int coluna;

    public Token() {
        this.lexema = "";
    }

    public TipoDeToken getTipo() {
        return tipo;
    }

    public void setTipo(TipoDeToken tipo) {
        this.tipo = tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

}
