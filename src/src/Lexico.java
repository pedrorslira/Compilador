
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Lexico {

    static final char EOF = '\uffff';
    static LinkedList<Token> tokens = new LinkedList<>();
    static boolean isComentarioAtivado = false;

    private static Lexico instanciaScanner = null;

    static String erroTokenNaoReconhecido = "O token lido não foi reconhecido.";
    static String erroNaoFechouAspas = "A aspas simples foi aberta mas não fechada.";
    static String erroCaractereNaoSuportado = "O caractere detectado não é suportado.";
    static String erroFloatMalFormado = "O número em float está mal formado.";
    static String erroComentarioNaoFechado = "O comentário multilinha não foi fechado.";
    static String erroExclamacao = "'!' sempre deve ser seguido de '='.";

    private Token token;
    private char caractere;
    private char lookahead;
    private int linha;
    private int coluna;
    private BufferedReader arquivo;

    private Lexico(String args) throws IOException {
        this.coluna = 0;
        this.linha = 1;
        this.token = new Token();
        this.arquivo = new BufferedReader(new FileReader(args));
        this.lookahead = (char) arquivo.read();
    }

    public static Lexico getInstanciaScanner(String args) throws IOException {
        if (instanciaScanner == null) {
            instanciaScanner = new Lexico(args);
        }
        return instanciaScanner;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public char getCaractere() {
        return caractere;
    }

    public void setCaractere(char caractere) {
        this.caractere = caractere;
    }

    public char getLookahead() {
        return lookahead;
    }

    public void setLookahead(char lookahead) {
        this.lookahead = lookahead;
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

    public BufferedReader getArquivo() {
        return arquivo;
    }

    public void setArquivo(BufferedReader arquivo) {
        this.arquivo = arquivo;
    }

    public Token scanner() throws IOException {
        token = new Token();
        proximoCaractere();

        while (!getNoBlank(caractere)) {
            proximoCaractere();
            if (caractere == '\n') {
                coluna = 0;
                linha++;
            }
        }
        analisarToken();
        if (isComentarioAtivado) {
            isComentarioAtivado = false;
            return scanner();
        }
        token.setLinha(linha);
        token.setColuna(coluna);
        tokens.add(token);
        //System.out.println(token.getTipo());
        return token;
    }

    public boolean getNoBlank(char caractere) {
        int ASCII = (int) caractere;
        return !(ASCII == 10 || ASCII == 9 || ASCII == 32 || ASCII == 13);
    }

    public void analisarToken() throws IOException {
        token.setLexema("");
        //Caracteres especiais
        if (caractere == ';') {
            token.setTipoToken(TipoDeToken.PontoEVirgula);
        } else if (caractere == '{') {
            token.setTipoToken(TipoDeToken.AbreChaves);
        } else if (caractere == '}') {
            token.setTipoToken(TipoDeToken.FechaChaves);
        } else if (caractere == '(') {
            token.setTipoToken(TipoDeToken.AbreParentese);
        } else if (caractere == ')') {
            token.setTipoToken(TipoDeToken.FechaParentese);
        } else if (caractere == ',') {
            token.setTipoToken(TipoDeToken.Virgula);
        } //Operandos Aritméticos/Comentários
        else if (caractere == '=' && lookahead != '=') {
            token.setTipoToken(TipoDeToken.Igual);
        } else if (caractere == '+') {
            token.setTipoToken(TipoDeToken.Soma);
        } else if (caractere == '-') {
            token.setTipoToken(TipoDeToken.Subtracao);
        } else if (caractere == '*') {
            token.setTipoToken(TipoDeToken.Multiplicacao);
        } else if (caractere == '/') {
            switch (lookahead) {
                case '/':
                    detectarComentarioLinhaUnica();
                    break;
                case '*':
                    detectarComentarioMultilinha();
                    break;
                default:
                    token.setTipoToken(TipoDeToken.Divisao);
                    break;
            }
        } //Operandos Relacionais
        else if (caractere == '>') {
            token.setTipoToken(TipoDeToken.Maior);
            if (lookahead == '=') {
                proximoCaractere();
                token.setTipoToken(TipoDeToken.MaiorIgual);
            }
        } else if (caractere == '<') {
            token.setTipoToken(TipoDeToken.Menor);
            if (lookahead == '=') {
                proximoCaractere();
                token.setTipoToken(TipoDeToken.MenorIgual);
            }
        } else if (caractere == '=' && lookahead == '=') {
            proximoCaractere();
            token.setTipoToken(TipoDeToken.IgualIgual);
        } else if (caractere == '!') {
            if (lookahead == '=') {
                proximoCaractere();
                token.setTipoToken(TipoDeToken.Diferente);
            } else {
                proximoCaractere();
                mensagemDeErro(erroExclamacao);
            }
        } else if (Character.isLetter(caractere) || caractere == '_') { //Palavras Reservadas/Id   
            while (Character.isLetter(lookahead) || lookahead == '_' || Character.isDigit(lookahead)) {
                token.setLexema(token.getLexema() + caractere);
                proximoCaractere();
            }
            token.setLexema(token.getLexema() + caractere);
            analisarLexema();

        } else if (Character.isDigit(caractere)) { //Dígitos Int,Float e Char
            token.setLexema("");

            while (Character.isDigit(lookahead)) {
                token.setLexema(token.getLexema() + caractere);
                proximoCaractere();
            }
            if (lookahead == '.') {
                token.setLexema(token.getLexema() + caractere);
                proximoCaractere();
                detectarFloat();
            } else {
                token.setLexema(token.getLexema() + caractere);
                token.setTipoToken(TipoDeToken.ValorInt);
            }
        } else if (caractere == '.') {
            detectarFloat();
        } else if (caractere == '\'') {
            token.setLexema(token.getLexema() + caractere);
            proximoCaractere();
            if (Character.isLetter(caractere) || Character.isDigit(caractere)) {
                token.setLexema(token.getLexema() + caractere);
                proximoCaractere();
                if (caractere == '\'') {
                    token.setLexema(token.getLexema() + caractere);
                    token.setTipoToken(TipoDeToken.ValorChar);
                } else {
                    proximoCaractere();
                    mensagemDeErro(erroNaoFechouAspas);
                    token.setTipoToken(TipoDeToken.Erro);
                }
            } else {
                proximoCaractere();
                mensagemDeErro(erroCaractereNaoSuportado);
            }
        } else if (caractere == EOF) { //End of File (EOF)
            token.setTipoToken(TipoDeToken.Fim);
        } else { //Token não reconhecido
            mensagemDeErro(erroTokenNaoReconhecido);
        }
        token.setLexemaInicial(token.getLexema());
    }

    public void proximoCaractere() throws IOException {
        coluna++;
        caractere = lookahead;
        lookahead = (char) arquivo.read();
    }

    public void analisarLexema() {
        switch (token.getLexema()) {
            case "main":
                token.setTipoToken(TipoDeToken.Main);
                break;
            case "int":
                token.setTipoToken(TipoDeToken.Int);
                break;
            case "float":
                token.setTipoToken(TipoDeToken.Float);
                break;
            case "char":
                token.setTipoToken(TipoDeToken.Char);
                break;
            case "if":
                token.setTipoToken(TipoDeToken.If);
                break;
            case "else":
                token.setTipoToken(TipoDeToken.Else);
                break;
            case "while":
                token.setTipoToken(TipoDeToken.While);
                break;
            case "do":
                token.setTipoToken(TipoDeToken.Do);
                break;
            case "for":
                token.setTipoToken(TipoDeToken.For);
                break;
            default:
                token.setTipoToken(TipoDeToken.Id);
                break;
        }
    }

    public void detectarFloat() throws IOException {
        token.setLexema(token.getLexema() + caractere);
        proximoCaractere();
        if (Character.isDigit(caractere)) {
            while (Character.isDigit(lookahead)) {
                token.setLexema(token.getLexema() + caractere);
                proximoCaractere();
            }
            token.setLexema(token.getLexema() + caractere);
            token.setTipoToken(TipoDeToken.ValorFloat);
        } else {
            mensagemDeErro(erroFloatMalFormado);
        }
    }

    public void detectarComentarioLinhaUnica() throws IOException {
        isComentarioAtivado = true;
        while (caractere != '\n') {
            proximoCaractere();
            if (caractere == EOF) {
                token.setTipoToken(TipoDeToken.Fim);
                break;
            }
        }
        coluna = 0;
        linha++;
    }

    public void detectarComentarioMultilinha() throws IOException {
        isComentarioAtivado = true;
        proximoCaractere();

        while (caractere != EOF) {
            if (caractere == '*' && lookahead == '/') {
                proximoCaractere();
                return;
            } else if (caractere == '\n') {
                coluna = 0;
                linha++;
            }
            proximoCaractere();
        }
        mensagemDeErro(erroComentarioNaoFechado);
    }

    public void mensagemDeErro(String mensagem) {
        if (tokens.isEmpty()) {
            System.out.println("Erro na linha " + linha + ",coluna " + (coluna) + ".Nenhum token chegou a ser lido" + "\n" + mensagem);
            token.setTipoToken(TipoDeToken.Erro);
            System.exit(-1);
        } else {
            System.out.println("Erro na linha " + linha + ",coluna " + (coluna) + ".Último token lido com sucesso: " + tokens.getLast().getTipoToken() + "\n" + mensagem);
            token.setTipoToken(TipoDeToken.Erro);
            System.exit(-1);
        }
    }
}
