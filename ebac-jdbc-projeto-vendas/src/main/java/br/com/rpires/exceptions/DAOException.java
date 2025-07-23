package br.com.rpires.exceptions;

/**
 * Exceção genérica para erros ocorridos na camada de acesso a dados (DAO).
 * encapsula SQLException ou outros problemas de persistência.
 */
public class DAOException extends Exception {
    private static final long serialVersionUID = 1L; // Identificador de versão para serialização

    /**
     * Construtor com mensagem e causa da exceção.
     * @param msg A mensagem descritiva da exceção.
     * @param e A causa raiz da exceção (ex: SQLException).
     */
    public DAOException(String msg, Throwable e) {
        super(msg, e);
    }

    /**
     * Construtor apenas com a mensagem da exceção.
     * @param msg A mensagem descritiva da exceção.
     */
    public DAOException(String msg) {
        super(msg);
    }
}