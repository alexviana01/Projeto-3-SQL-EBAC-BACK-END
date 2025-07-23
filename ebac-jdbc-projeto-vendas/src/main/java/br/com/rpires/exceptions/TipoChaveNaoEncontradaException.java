package br.com.rpires.exceptions;

/**
 * Exceção lançada quando a "chave lógica" de uma entidade (definida pela anotação @TipoChave)
 * não é encontrada ou o método getter correspondente não pode ser invocado.
 */
public class TipoChaveNaoEncontradaException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Construtor apenas com a mensagem da exceção.
     * @param msg A mensagem descritiva da exceção.
     */
    public TipoChaveNaoEncontradaException(String msg) {
        super(msg);
    }

    /**
     * Construtor com mensagem e causa da exceção.
     * @param msg A mensagem descritiva da exceção.
     * @param e A causa raiz da exceção.
     */
    public TipoChaveNaoEncontradaException(String msg, Throwable e) {
        super(msg, e);
    }
}