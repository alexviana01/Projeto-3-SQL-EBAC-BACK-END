package br.com.rpires.exceptions;

/**
 * Exceção lançada quando o tipo de um atributo de domínio não é reconhecido
 * ou suportado pelo mecanismo de mapeamento de tipos do banco de dados (ex: Integer, String, BigDecimal).
 */
public class TipoElementoNaoConhecidoException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Construtor com a mensagem da exceção.
     * @param msg A mensagem descritiva da exceção.
     */
    public TipoElementoNaoConhecidoException(String msg) {
        super(msg);
    }
}