package br.com.rpires.domain;

/**
 * Interface que define um contrato para todas as entidades que podem ser persistidas
 * no banco de dados. Garante que cada entidade tenha um ID único.
 */
public interface Persistente {
    /**
     * Retorna o ID (chave primária técnica) da entidade.
     * @return O ID da entidade.
     */
    Long getId();

    /**
     * Define o ID (chave primária técnica) da entidade.
     * Usado principalmente após a inserção para receber o ID gerado pelo banco.
     * @param id O ID a ser definido.
     */
    void setId(Long id);
}