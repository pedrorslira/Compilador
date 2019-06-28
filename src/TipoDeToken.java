
public enum TipoDeToken {
    Main, Int, Float, Char, If, Else, While, Do, For, //palavras reservadas
    PontoEVirgula, Virgula, AbreChaves, FechaChaves, AbreParentese, FechaParentese, //caracteres especiais
    Igual, Soma, Subtracao, Multiplicacao, Divisao, //operandos aritméticos
    Diferente, IgualIgual, Maior, Menor, MaiorIgual, MenorIgual, //operandos relacionais
    ValorInt, ValorFloat, ValorChar, //valores
    Id, Erro, Fim //outros   
}
