
import java.io.IOException;

public class Parser {

    private static Parser instanciaParser = null;

    //Erros do Sintático:
    static String erroFuncaoMain = "A função main está mal formada.";
    static String erroAbrirBloco = "O bloco não foi aberto.";
    static String erroFecharBloco = "O bloco não foi fechado.";
    static String erroPontoEVirgula = "Faltou o ponto e vírgula no final da instrução.";
    static String erroTermo = "O Termo está mal formado.";
    static String erroFator = "O Fator está mal formado.";
    static String erroExpressaoAritmetica = "A expressão aritmética está mal formada.";
    static String erroComando = "O comando está mal formado.";
    static String erroComandoBasico = "O comando básico está mal formado.";
    static String erroIteracao = "A iteração está mal formada.";
    static String erroDeclaracao = "A declaração de variável está mal formada.";
    static String erroFim = "O fim de arquivo não foi encontrado.";
    static String erroDeclaracaoNaoPermitida = "Declaração de variável não permitida aqui.";
    static String erroElseSemIf = "O else não tem um if correspondente.";
    static String erroFaltaComando = "Está faltando comando nessa operação.";

    //Erros do Semântico:
    static String erroTipoIncompativel = "Os tipos são incompatíveis.";
    static String erroAtribuicaoFloatComInt = "Não se pode atribuir um float a um int.";
    static String erroVariavelNaoDeclarada = "A variável está sendo usada sem ser declarada.";
    static String erroVariavelDeclaradaJaExiste = "A variável que foi declarada já existe nesse escopo.";
    static String erroDivisao = "Tipos incompatíveis.O resultado da divisão é dado em float.";

    static TipoDeToken tipo1, tipo2;
    static Token auxConversao = null;
    static boolean flagAtribuicao = false;

    private Lexico lexico;
    private Token token;
    private Token lookahead;
    private TabelaDeSimbolos tabela;
    private int contadorTemp, contadorLabel;
    private String temp, tempAux, label, labelAux;

    private Parser(String args) throws IOException {
        lexico = Lexico.getInstanciaScanner(args);
        lookahead = lexico.scanner();
        tabela = new TabelaDeSimbolos();
        contadorTemp = -1;
        contadorLabel = -1;
    }

    public static Parser getInstanciaParser(String args) throws IOException {
        if (instanciaParser == null) {
            instanciaParser = new Parser(args);
        }
        return instanciaParser;
    }

    public void programa() throws IOException {
        nextToken();
        if (token.getTipoToken() == TipoDeToken.Fim) {
            mensagemDeErro(erroFuncaoMain);
        }
        if (token.getTipoToken() != TipoDeToken.Int) {
            mensagemDeErro(erroFuncaoMain);
        }
        nextToken();
        if (token.getTipoToken() != TipoDeToken.Main) {
            mensagemDeErro(erroFuncaoMain);
        }
        nextToken();
        if (token.getTipoToken() != TipoDeToken.AbreParentese) {
            mensagemDeErro(erroFuncaoMain);
        }
        nextToken();
        if (token.getTipoToken() != TipoDeToken.FechaParentese) {
            mensagemDeErro(erroFuncaoMain);
        }
        nextToken();
        isBloco();
        nextToken();
        if (token.getTipoToken() != TipoDeToken.Fim) {
            mensagemDeErro(erroFim);
        }
    }

    public boolean isBloco() throws IOException {
        if (token.getTipoToken() != TipoDeToken.AbreChaves) {
            mensagemDeErro(erroAbrirBloco);
        }
        tabela.add(token);
        nextToken();
        if (isTipo()) {
            while (true) {
                if (isDeclaracaoVariavel()) {
                    nextToken();
                    if (!isTipo()) {
                        break;
                    }
                    continue;
                }
                break;
            }
        }
        if (isFirstComando()) {
            while (true) {
                if (isComando()) {
                    nextToken();
                    if (!isFirstComando()) {
                        if (isTipo()) {
                            mensagemDeErro(erroDeclaracaoNaoPermitida);
                        }
                        break;
                    }
                    continue;
                }
                break;
            }
        }
        if (token.getTipoToken() == TipoDeToken.Else) {
            mensagemDeErro(erroElseSemIf);
        }
        if (token.getTipoToken() != TipoDeToken.FechaChaves) {
            mensagemDeErro(erroFecharBloco);
        }
        tabela.desativarBloco();
        return true;
    }

    public boolean isComando() throws IOException {
        boolean flagRetorno = false;
        if (token.getTipoToken() == TipoDeToken.Id || token.getTipoToken() == TipoDeToken.AbreChaves) {
            if (isComandoBasico()) {
                return true;
            } else {
                mensagemDeErro(erroComandoBasico);
            }
        } else if (token.getTipoToken() == TipoDeToken.While || token.getTipoToken() == TipoDeToken.Do) {
            if (isIteracao()) {
                return true;
            } else {
                mensagemDeErro(erroIteracao);
            }
        } else if (token.getTipoToken() == TipoDeToken.If) {
            nextToken();
            if (token.getTipoToken() == TipoDeToken.AbreParentese) {
                nextToken();
                if (isExpressaoRelacional()) {
                    label = newLabel();
                    System.out.println("if " + tempAux + " " + "==" + " false goto " + label);
                    if (token.getTipoToken() == TipoDeToken.FechaParentese) {
                        nextToken();
                        if (token.getTipoToken() == TipoDeToken.Else) {
                            mensagemDeErro(erroFaltaComando);
                        }
                        if (!isFirstComando() && token.getTipoToken() != TipoDeToken.Int && token.getTipoToken() != TipoDeToken.Float && token.getTipoToken() != TipoDeToken.Char) {
                            mensagemDeErro(erroFaltaComando);
                        }
                        String aux = label;
                        if (isComando()) {
                            flagRetorno = true;
                            if (lookahead.getTipoToken() == TipoDeToken.Else) {
                                nextToken();
                            } else {
                                System.out.println(aux + ":");
                            }
                            if (token.getTipoToken() == TipoDeToken.Else) {
                                nextToken();
                                labelAux = label;
                                label = newLabel();
                                System.out.println("goto " + label);
                                System.out.println(aux + ":");
                                if (!isFirstComando()) {
                                    mensagemDeErro(erroFaltaComando);
                                }
                                if (isComando()) {
                                    System.out.println(label + ":");
                                    return true;
                                }
                            }
                        }

                    }
                }
            }
        } else if (token.getTipoToken() == TipoDeToken.Int || token.getTipoToken() == TipoDeToken.Float || token.getTipoToken() == TipoDeToken.Char) {
            mensagemDeErro(erroDeclaracaoNaoPermitida);
        }
        return flagRetorno;
    }

    public boolean isComandoBasico() throws IOException {
        if (token.getTipoToken() == TipoDeToken.Id) {
            if (isAtribuicao()) {
                return true;
            }
        } else if (token.getTipoToken() == TipoDeToken.AbreChaves) {
            if (isBloco()) {
                return true;
            }
        }
        return false;
    }

    public boolean isIteracao() throws IOException {
        if (token.getTipoToken() == TipoDeToken.While) {
            label = newLabel();
            System.out.println(label + ":");
            nextToken();
            if (token.getTipoToken() == TipoDeToken.AbreParentese) {
                nextToken();
                if (isExpressaoRelacional()) {
                    labelAux = label;
                    label = newLabel();
                    System.out.println("if " + tempAux + " " + "==" + " false goto " + label);
                    if (token.getTipoToken() == TipoDeToken.FechaParentese) {
                        nextToken();
                        if (!isFirstComando()) {
                            mensagemDeErro(erroFaltaComando);
                        }
                        String aux = label;
                        String aux2 = labelAux;
                        if (isComando()) {
                            System.out.println("goto " + aux2);
                            System.out.println(aux + ":");
                            return true;
                        }
                    }
                }
            }
            return false;
        } else if (token.getTipoToken() == TipoDeToken.Do) {
            label = newLabel();
            System.out.println(label + ":");
            nextToken();
            String aux3 = label;
            if (isComando()) {
                nextToken();
                if (token.getTipoToken() == TipoDeToken.While) {
                    nextToken();
                    if (token.getTipoToken() == TipoDeToken.AbreParentese) {
                        nextToken();
                        if (isExpressaoRelacional()) {
                            System.out.println("if " + tempAux + " " + "!=" + " false goto " + aux3);
                            if (token.getTipoToken() == TipoDeToken.FechaParentese) {
                                nextToken();
                                if (token.getTipoToken() == TipoDeToken.PontoEVirgula) {
                                    return true;
                                } else {
                                    mensagemDeErro(erroPontoEVirgula);
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isAtribuicao() throws IOException {
        Token auxAtribuicao;
        TipoDeToken tipo;
        Token aux;
        flagAtribuicao = true;
        if (token.getTipoToken() == TipoDeToken.Id) {
            tipo = tabela.buscarTipo(token);
            if (tipo == null) {
                mensagemDeErro(erroVariavelNaoDeclarada);
            }
            auxAtribuicao = token;
            tipo1 = tipo;
            nextToken();
            if (token.getTipoToken() == TipoDeToken.Igual) {
                nextToken();
                aux = expressaoAritmetica();
                if (aux.getTipoToken() == TipoDeToken.Id) {
                    tipo = tabela.buscarTipo(aux);
                    if (tipo == null) {
                        mensagemDeErro(erroVariavelNaoDeclarada);
                    }
                }
                flagAtribuicao = false;
                System.out.println(auxAtribuicao.getLexema() + "=" + aux.getLexema());
                if (token.getTipoToken() == TipoDeToken.PontoEVirgula) {
                    return true;
                } else {
                    mensagemDeErro(erroPontoEVirgula);
                }

            }
        }
        return false;
    }

    public boolean isExpressaoRelacional() throws IOException {
        String opRelacional;
        boolean flagRetorno = false;
        Token aux, aux2;
        if (token.getTipoToken() == TipoDeToken.Id) {
            TipoDeToken tipo = tabela.buscarTipo(token);
            if (tipo == null) {
                mensagemDeErro(erroVariavelNaoDeclarada);
            }
            tipo1 = tipo;
        } else {
            tipo1 = token.getTipo();
        }
        aux = expressaoAritmetica();
        if (isOperandoRelacional()) {
            opRelacional = operandoRelacional();
            nextToken();
            aux2 = expressaoAritmetica();
            temp = newTemp();
            tempAux = temp;
            System.out.println(temp + "=" + aux.getLexema() + opRelacional + aux2.getLexema());
            flagRetorno = true;
        }
        return flagRetorno;
    }

    public Token expressaoAritmetica() throws IOException {
        char op;
        Token aux = null, aux2 = null;
        if (token.getTipoToken() == TipoDeToken.Id || token.getTipoToken() == TipoDeToken.ValorFloat || token.getTipoToken() == TipoDeToken.ValorInt
                || token.getTipoToken() == TipoDeToken.ValorChar || token.getTipoToken() == TipoDeToken.AbreParentese) {
            aux = termo();
            while (token.getTipoToken() == TipoDeToken.Soma || token.getTipoToken() == TipoDeToken.Subtracao) {
                if (token.getTipoToken() == TipoDeToken.Soma) {
                    op = '+';
                } else {
                    op = '-';
                }
                nextToken();
                aux2 = termo();
                if (converter(aux, op, aux2)) {
                    continue;
                }
                temp = newTemp();
                System.out.println(temp + "=" + aux.getLexema() + op + aux2.getLexema());
                aux.setLexema(temp);
            }
        } else {
            mensagemDeErro(erroExpressaoAritmetica);
        }
        return aux;
    }

    public Token termo() throws IOException {
        char op;
        Token aux = null, aux2 = null;
        if (token.getTipoToken() == TipoDeToken.Id || token.getTipoToken() == TipoDeToken.ValorFloat || token.getTipoToken() == TipoDeToken.ValorInt
                || token.getTipoToken() == TipoDeToken.ValorChar || token.getTipoToken() == TipoDeToken.AbreParentese) {
            aux = fator();
            nextToken();
            while (token.getTipoToken() == TipoDeToken.Multiplicacao || token.getTipoToken() == TipoDeToken.Divisao) {
                if (token.getTipoToken() == TipoDeToken.Divisao && tipo1 == TipoDeToken.Int) {
                    mensagemDeErro(erroDivisao);
                }
                if (token.getTipoToken() == TipoDeToken.Multiplicacao) {
                    op = '*';
                } else {
                    op = '/';
                }
                nextToken();
                aux2 = fator();
                nextToken();
                if (converter(aux, op, aux2)) {
                    continue;
                }
                gerarCodigoTermo(aux, op, aux2);
            }
        } else {
            mensagemDeErro(erroTermo);
        }
        return aux;
    }

    public Token fator() throws IOException {
        TipoDeToken tipo;
        Token aux;
        if (token.getTipoToken() == TipoDeToken.Id) {
            tipo = tabela.buscarTipo(token);
            if (tipo == null) {
                mensagemDeErro(erroVariavelNaoDeclarada);
            }
            tipo2 = tipo;
            checarTiposId();
            return token;
        } else if (token.getTipoToken() == TipoDeToken.ValorFloat || token.getTipoToken() == TipoDeToken.ValorInt || token.getTipoToken() == TipoDeToken.ValorChar) {
            tipo2 = token.getTipoToken();
            checarTipos();
            return token;
        } else if (token.getTipoToken() == TipoDeToken.AbreParentese) {
            nextToken();
            aux = expressaoAritmetica();
            if (token.getTipoToken() == TipoDeToken.FechaParentese) {
                return aux;
            }
        } else {
            mensagemDeErro(erroFator);
        }
        return token;
    }

    public boolean isDeclaracaoVariavel() throws IOException {
        TipoDeToken tipo;
        if (token.getTipoToken() == TipoDeToken.Int || token.getTipoToken() == TipoDeToken.Float || token.getTipoToken() == TipoDeToken.Char) {
            tipo = token.getTipoToken();
            nextToken();
            if (token.getTipoToken() == TipoDeToken.Id) {
                if (tabela.buscarTipoEscopo(token) != null) {
                    mensagemDeErro(erroVariavelDeclaradaJaExiste);
                }
                token.setTipo(tipo);
                tabela.add(token);
                nextToken();
                while (token.getTipoToken() == TipoDeToken.Virgula) {
                    nextToken();
                    if (token.getTipoToken() == TipoDeToken.Id) {
                        if (tabela.buscarTipoEscopo(token) != null) {
                            mensagemDeErro(erroVariavelDeclaradaJaExiste);
                        }
                        token.setTipo(tipo);
                        tabela.add(token);
                        nextToken();
                    } else {
                        mensagemDeErro(erroDeclaracao);
                    }
                }
                if (token.getTipoToken() == TipoDeToken.PontoEVirgula) {
                    return true;
                } else {
                    mensagemDeErro(erroPontoEVirgula);
                }
            }
        }
        mensagemDeErro(erroDeclaracao);
        return false;
    }

    public boolean isOperandoRelacional() {
        if (token.getTipoToken() == TipoDeToken.IgualIgual
                || token.getTipoToken() == TipoDeToken.Diferente
                || token.getTipoToken() == TipoDeToken.Maior
                || token.getTipoToken() == TipoDeToken.Menor
                || token.getTipoToken() == TipoDeToken.MaiorIgual
                || token.getTipoToken() == TipoDeToken.MenorIgual) {
            return true;
        }
        return false;
    }

    public String operandoRelacional() {
        if (token.getTipoToken() == TipoDeToken.IgualIgual) {
            return "==";
        } else if (token.getTipoToken() == TipoDeToken.Diferente) {
            return "!=";
        } else if (token.getTipoToken() == TipoDeToken.Maior) {
            return ">";
        } else if (token.getTipoToken() == TipoDeToken.Menor) {
            return "<";
        } else if (token.getTipoToken() == TipoDeToken.MaiorIgual) {
            return ">=";
        } else if (token.getTipoToken() == TipoDeToken.MenorIgual) {
            return "<=";
        }
        return null;
    }

    public boolean isTipo() {
        if (token.getTipoToken() == TipoDeToken.Int || token.getTipoToken() == TipoDeToken.Float || token.getTipoToken() == TipoDeToken.Char) {
            return true;
        }
        return false;
    }

    public boolean isFirstComando() {
        if (token.getTipoToken() == TipoDeToken.Id || token.getTipoToken() == TipoDeToken.AbreChaves || token.getTipoToken() == TipoDeToken.While
                || token.getTipoToken() == TipoDeToken.Do || token.getTipoToken() == TipoDeToken.If) {
            return true;
        }
        return false;
    }

    public void nextToken() throws IOException {
        token = lookahead;
        lookahead = lexico.scanner();
    }

    public void checarTipos() {
        if ((tipo1 != TipoDeToken.Char && tipo2 == TipoDeToken.ValorChar) || (tipo1 == TipoDeToken.Char && tipo2 != TipoDeToken.ValorChar)) {
            mensagemDeErroTipos(erroTipoIncompativel);
        }
        if (flagAtribuicao == true) {
            if ((tipo1 == TipoDeToken.Int && tipo2 == TipoDeToken.ValorFloat)) {
                mensagemDeErro(erroAtribuicaoFloatComInt);
            }
        }
    }

    public void checarTiposId() {
        if ((tipo1 != TipoDeToken.Char && tipo2 == TipoDeToken.Char) || (tipo1 == TipoDeToken.Char && tipo2 != TipoDeToken.Char)) {
            mensagemDeErroTipos(erroTipoIncompativel);
        }
        if (flagAtribuicao == true) {
            if ((tipo1 == TipoDeToken.Int && tipo2 == TipoDeToken.Float)) {
                mensagemDeErro(erroAtribuicaoFloatComInt);
            }
        }
    }

    public void gerarCodigoTermo(Token aux, char op, Token aux2) {
        temp = newTemp();
        System.out.println(temp + "=" + aux.getLexema() + op + aux2.getLexema());
        aux.setLexema(temp);
    }

    public boolean converter(Token aux, char op, Token aux2) {
        TipoDeToken tipo = tabela.buscarTipo(aux);
        if (tipo1 == TipoDeToken.Float) {
            if (op == '/') {
                if (tipo == TipoDeToken.Int || aux.getTipoToken() == TipoDeToken.ValorInt) {
                    temp = newTemp();
                    System.out.println(temp + "=float " + aux.getLexema());
                    aux.setTipoToken(TipoDeToken.ValorFloat);
                    aux.setLexema(temp);
                }
                if (tipo2 == TipoDeToken.Int || aux2.getTipoToken() == TipoDeToken.ValorInt) {
                    temp = newTemp();
                    System.out.println(temp + "=float " + aux2.getLexema());
                    aux2.setTipoToken(TipoDeToken.ValorFloat);
                    aux2.setLexema(temp);
                }
                gerarCodigoTermo(aux, op, aux2);
                return true;
            }
            if (auxConversao == null) {
                if ((tipo == TipoDeToken.Int || aux.getTipoToken() == TipoDeToken.ValorInt) && (tipo2 == TipoDeToken.Int || aux2.getTipoToken() == TipoDeToken.ValorInt)) {
                    gerarCodigoTermo(aux, op, aux2);
                    temp = newTemp();
                    System.out.println(temp + "=float " + aux.getLexema());
                    aux.setLexema(temp);
                    auxConversao = aux;
                    auxConversao.setTipoToken(TipoDeToken.ValorFloat);
                    return true;
                }
            } else {
                if (aux.getLexema().equals(auxConversao.getLexema())) {
                    aux = auxConversao;
                } else if (aux2.getLexema().equals(auxConversao.getLexema())) {
                    aux2 = auxConversao;
                }
            }
            if ((tipo == TipoDeToken.Int || aux.getTipoToken() == TipoDeToken.ValorInt) && (tipo2 != TipoDeToken.Int && aux2.getTipoToken() != TipoDeToken.ValorInt)) {
                temp = newTemp();
                System.out.println(temp + "=float " + aux.getLexema());
                aux.setLexema(temp);
                gerarCodigoTermo(aux, op, aux2);
                auxConversao = null;
                return true;
            }
            if ((tipo != TipoDeToken.Int && aux.getTipoToken() != TipoDeToken.ValorInt) && (tipo2 == TipoDeToken.Int || aux2.getTipoToken() == TipoDeToken.ValorInt)) {
                temp = newTemp();
                System.out.println(temp + "=float " + aux2.getLexema());
                aux2.setLexema(temp);
                gerarCodigoTermo(aux, op, aux2);
                auxConversao = null;
                return true;
            }
        }
        return false;
    }

    public String newTemp() {
        contadorTemp++;
        return "t" + contadorTemp;
    }

    public String newLabel() {
        contadorLabel++;
        return "L" + contadorLabel;
    }

    public void mensagemDeErro(String mensagem) {
        System.out.println("Erro na linha " + token.getLinha() + ",coluna " + token.getColuna() + ".Último token lido com sucesso: " + token.getTipoToken() + "\n" + mensagem);
        token.setTipoToken(TipoDeToken.Erro);
        System.exit(-1);
    }

    public void mensagemDeErroTipos(String mensagem) {
        System.out.println("Erro na linha " + token.getLinha() + ",coluna " + token.getColuna() + ".Último token lido com sucesso: " + token.getTipoToken() + "\n" + mensagem);
        System.out.println(tipo2 + " não é compatível com " + tipo1 + ".");
        token.setTipoToken(TipoDeToken.Erro);
        System.exit(-1);
    }

}
