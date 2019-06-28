
public class Token {

    private TipoDeToken tipoToken;
    private TipoDeToken tipo;
    private String lexema;
    private String lexemaInicial;
    private int linha;
    private int coluna;

    public Token() {
        this.lexema = "";
    }

    public TipoDeToken getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(TipoDeToken tipoToken) {
        this.tipoToken = tipoToken;
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

    public String getLexemaInicial() {
        return lexemaInicial;
    }

    public void setLexemaInicial(String lexemaInicial) {
        this.lexemaInicial = lexemaInicial;
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
