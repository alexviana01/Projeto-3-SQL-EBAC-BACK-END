package br.com.rpires.exceptions;

/**
 * Exceção lançada quando uma consulta que esperava retornar apenas um registro
 * retorna mais de um. Indica um possível problema de unicidade no banco de dados.
 */
public class MaisDeUmRegistroException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Construtor com a mensagem da exceção.
     * @param msg A mensagem descritiva da exceção.
     */
    public MaisDeUmRegistroException(String msg) {
        super(msg);
    }
}