
import java.io.IOException;

public class Parser {

    static String erroFuncaoMain = "A função main está mal formada";
    static String erroAbrirBloco = "O bloco não foi aberto";
    static String erroFecharBloco = "O bloco não foi fechado";
    static String erroPontoEVirgula = "Faltou o ponto e vírgula no final da instrução";
    static String erroTermo = "O Termo está mal formado";
    static String erroFator = "O Fator está mal formado";
    static String erroExpressaoAritmetica = "A expressão aritmética está mal formada";
    static String erroComando = "O comando está mal formado";
    static String erroComandoBasico = "O comando básico está mal formado";
    static String erroIteracao = "A iteração está mal formada";
    static String erroDeclaracao = "A declaração de variável está mal formada";
    static String erroFim = "O fim de arquivo não foi encontrado.";
    static String erroDeclaracaoNaoPermitida = "Declaração de variável não permitida aqui";
    static String erroElseSemIf = "O else não tem um if correspondente";
    static String erroFaltaComando = "Está faltando comando nessa operação";

    private Lexico lexico;
    private Token token;
    private Token lookahead;

    public Parser(String args) throws IOException {
        lexico = Lexico.getInstanciaScanner(args);
        lookahead = lexico.scanner();
    }

    public void programa() throws IOException {
        nextToken();
        if (token.getTipo() == TipoDeToken.Fim) {
            mensagemDeErro(erroFuncaoMain);
        }
        if (token.getTipo() != TipoDeToken.Int) {
            mensagemDeErro(erroFuncaoMain);
        }
        nextToken();
        if (token.getTipo() != TipoDeToken.Main) {
            mensagemDeErro(erroFuncaoMain);
        }
        nextToken();
        if (token.getTipo() != TipoDeToken.AbreParentese) {
            mensagemDeErro(erroFuncaoMain);
        }
        nextToken();
        if (token.getTipo() != TipoDeToken.FechaParentese) {
            mensagemDeErro(erroFuncaoMain);
        }
        nextToken();
        isBloco();
        nextToken();
        if (token.getTipo() != TipoDeToken.Fim) {
            mensagemDeErro(erroFim);
        }
    }

    public boolean isBloco() throws IOException {
        if (token.getTipo() != TipoDeToken.AbreChaves) {
            mensagemDeErro(erroAbrirBloco);
        }
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
        if (token.getTipo() == TipoDeToken.Else) {
            mensagemDeErro(erroElseSemIf);
        }
        if (token.getTipo() != TipoDeToken.FechaChaves) {
            mensagemDeErro(erroFecharBloco);
        }
        return true;
    }

    public boolean isComando() throws IOException {
        boolean flagRetorno = false;
        if (token.getTipo() == TipoDeToken.Id || token.getTipo() == TipoDeToken.AbreChaves) {
            if (isComandoBasico()) {
                return true;
            } else {
                mensagemDeErro(erroComandoBasico);
            }
        } else if (token.getTipo() == TipoDeToken.While || token.getTipo() == TipoDeToken.Do) {
            if (isIteracao()) {
                return true;
            } else {
                mensagemDeErro(erroIteracao);
            }
        } else if (token.getTipo() == TipoDeToken.If) {
            nextToken();
            if (token.getTipo() == TipoDeToken.AbreParentese) {
                nextToken();
                if (isExpressaoRelacional()) {
                    if (token.getTipo() == TipoDeToken.FechaParentese) {
                        nextToken();
                        if (token.getTipo() == TipoDeToken.Else) {
                            mensagemDeErro(erroFaltaComando);
                        }
                        if (!isFirstComando() && token.getTipo() != TipoDeToken.Int && token.getTipo() != TipoDeToken.Float && token.getTipo() != TipoDeToken.Char) {
                            mensagemDeErro(erroFaltaComando);
                        }
                        if (isComando()) {
                            flagRetorno = true;
                            if (lookahead.getTipo() == TipoDeToken.Else) {
                                nextToken();
                            }
                            if (token.getTipo() == TipoDeToken.Else) {
                                nextToken();
                                if (!isFirstComando()) {
                                    mensagemDeErro(erroFaltaComando);
                                }
                                if (isComando()) {
                                    return true;
                                }
                            }
                        }

                    }
                }
            }
        } else if (token.getTipo() == TipoDeToken.Int || token.getTipo() == TipoDeToken.Float || token.getTipo() == TipoDeToken.Char) {
            mensagemDeErro(erroDeclaracaoNaoPermitida);
        }
        return flagRetorno;
    }

    public boolean isComandoBasico() throws IOException {
        if (token.getTipo() == TipoDeToken.Id) {
            if (isAtribuicao()) {
                return true;
            }
        } else if (token.getTipo() == TipoDeToken.AbreChaves) {
            if (isBloco()) {
                return true;
            }
        }
        return false;
    }

    public boolean isIteracao() throws IOException {
        if (token.getTipo() == TipoDeToken.While) {
            nextToken();
            if (token.getTipo() == TipoDeToken.AbreParentese) {
                nextToken();
                if (isExpressaoRelacional()) {
                    if (token.getTipo() == TipoDeToken.FechaParentese) {
                        nextToken();
                        if (!isFirstComando()) {
                            mensagemDeErro(erroFaltaComando);
                        }
                        if (isComando()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else if (token.getTipo() == TipoDeToken.Do) {
            nextToken();
            if (isComando()) {
                nextToken();
                if (token.getTipo() == TipoDeToken.While) {
                    nextToken();
                    if (token.getTipo() == TipoDeToken.AbreParentese) {
                        nextToken();
                        if (isExpressaoRelacional()) {
                            if (token.getTipo() == TipoDeToken.FechaParentese) {
                                nextToken();
                                if (token.getTipo() == TipoDeToken.PontoEVirgula) {
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
        if (token.getTipo() == TipoDeToken.Id) {
            nextToken();
            if (token.getTipo() == TipoDeToken.Igual) {
                nextToken();
                if (isExpressaoAritmetica()) {
                    if (token.getTipo() == TipoDeToken.PontoEVirgula) {
                        return true;
                    } else {
                        mensagemDeErro(erroPontoEVirgula);
                    }
                }
            }
        }
        return false;
    }

    public boolean isExpressaoRelacional() throws IOException {
        boolean flagRetorno = false;
        if (isExpressaoAritmetica()) {
            if (isOperandoRelacional()) {
                nextToken();
                if (!isExpressaoAritmetica()) {
                    mensagemDeErro(erroExpressaoAritmetica);
                } else {
                    flagRetorno = true;
                }
            }
        }
        return flagRetorno;
    }

    public boolean isExpressaoAritmetica() throws IOException {
        boolean flagRetorno = false;
        if (token.getTipo() == TipoDeToken.Id || token.getTipo() == TipoDeToken.ValorFloat || token.getTipo() == TipoDeToken.ValorInt
                || token.getTipo() == TipoDeToken.ValorChar || token.getTipo() == TipoDeToken.AbreParentese) {
            if (isTermo()) {
                flagRetorno = true;
            }
            while (token.getTipo() == TipoDeToken.Soma || token.getTipo() == TipoDeToken.Subtracao) {
                nextToken();
                if (!isTermo()) {
                    mensagemDeErro(erroTermo);
                }
            }
        }
        return flagRetorno;
    }

    public boolean isTermo() throws IOException {
        boolean flagRetorno = false;
        if (token.getTipo() == TipoDeToken.Id || token.getTipo() == TipoDeToken.ValorFloat || token.getTipo() == TipoDeToken.ValorInt
                || token.getTipo() == TipoDeToken.ValorChar || token.getTipo() == TipoDeToken.AbreParentese) {
            if (isFator()) {
                flagRetorno = true;
                nextToken();
            }
            while (token.getTipo() == TipoDeToken.Multiplicacao || token.getTipo() == TipoDeToken.Divisao) {
                nextToken();
                if (!isFator()) {
                    mensagemDeErro(erroFator);
                }
                nextToken();
            }
        }
        return flagRetorno;
    }

    public boolean isFator() throws IOException {
        if (token.getTipo() == TipoDeToken.Id || token.getTipo() == TipoDeToken.ValorFloat || token.getTipo() == TipoDeToken.ValorInt || token.getTipo() == TipoDeToken.ValorChar) {
            return true;
        } else if (token.getTipo() == TipoDeToken.AbreParentese) {
            nextToken();
            if (isExpressaoAritmetica()) {
                if (token.getTipo() == TipoDeToken.FechaParentese) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDeclaracaoVariavel() throws IOException {
        if (token.getTipo() == TipoDeToken.Int || token.getTipo() == TipoDeToken.Float || token.getTipo() == TipoDeToken.Char) {
            nextToken();
            if (token.getTipo() == TipoDeToken.Id) {
                nextToken();
                while (token.getTipo() == TipoDeToken.Virgula) {
                    nextToken();
                    if (token.getTipo() == TipoDeToken.Id) {
                        nextToken();
                    } else {
                        mensagemDeErro(erroDeclaracao);
                    }
                }
                if (token.getTipo() == TipoDeToken.PontoEVirgula) {
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
        if (token.getTipo() == TipoDeToken.IgualIgual
                || token.getTipo() == TipoDeToken.Diferente
                || token.getTipo() == TipoDeToken.Maior
                || token.getTipo() == TipoDeToken.Menor
                || token.getTipo() == TipoDeToken.MaiorIgual
                || token.getTipo() == TipoDeToken.MenorIgual) {
            return true;
        }
        return false;
    }

    public boolean isTipo() {
        if (token.getTipo() == TipoDeToken.Int || token.getTipo() == TipoDeToken.Float || token.getTipo() == TipoDeToken.Char) {
            return true;
        }
        return false;
    }

    public boolean isFirstComando() {
        if (token.getTipo() == TipoDeToken.Id || token.getTipo() == TipoDeToken.AbreChaves || token.getTipo() == TipoDeToken.While
                || token.getTipo() == TipoDeToken.Do || token.getTipo() == TipoDeToken.If) {
            return true;
        }
        return false;
    }

    public void nextToken() throws IOException {
        token = lookahead;
        lookahead = lexico.scanner();
        System.out.println(token.getTipo());
    }

    public void mensagemDeErro(String mensagem) {
        System.out.println("Erro na linha " + token.getLinha() + ",coluna " + token.getColuna() + ".Último token lido com sucesso: " + token.getTipo() + "\n" + mensagem);
        token.setTipo(TipoDeToken.Erro);
        System.exit(-1);
    }
}
