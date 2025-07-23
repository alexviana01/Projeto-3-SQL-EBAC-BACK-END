package br.com.rpires.exceptions;

/**
 * Exceção lançada quando há um problema no mapeamento da tabela de uma entidade,
 * geralmente devido à falta ou configuração incorreta da anotação @Tabela.
 */
public class TableException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Construtor com a mensagem da exceção.
     * @param msg A mensagem descritiva da exceção.
     */
    public TableException(String msg) {
        super(msg);
    }
}