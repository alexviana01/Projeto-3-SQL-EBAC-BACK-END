package br.com.rpires.dao.generic;

//Linha CORRETA
import br.com.rpires.domain.Persistente;

import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.MaisDeUmRegistroException;
import br.com.rpires.exceptions.TableException;
import br.com.rpires.exceptions.TipoChaveNaoEncontradaException;

import java.io.Serializable;
import java.util.Collection;

/**
 * Interface genérica para a camada de acesso a dados (DAO - Data Access Object).
 * Define as operações CRUD (Create, Read, Update, Delete) básicas para qualquer
 * entidade persistível.
 *
 * @param <T> O tipo da entidade que implementa a interface Persistente.
 * @param <E> O tipo da chave primária (identificador) da entidade.
 */
public interface IGenericDAO <T extends Persistente, E extends Serializable> {

    /**
     * Cadastra um novo registro no banco de dados.
     * @param entity A entidade a ser cadastrada.
     * @return {@code true} se o cadastro foi bem-sucedido, {@code false} caso contrário.
     * @throws TipoChaveNaoEncontradaException Se a chave primária do objeto não for encontrada ou configurada.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    Boolean cadastrar(T entity) throws TipoChaveNaoEncontradaException, DAOException;

    /**
     * Exclui um registro do banco de dados com base em sua chave primária lógica.
     * @param valor A chave primária (identificador) do dado a ser excluído.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    void excluir(E valor) throws DAOException;

    /**
     * Altera um registro existente no banco de dados.
     * @param entity A entidade com os dados atualizados.
     * @throws TipoChaveNaoEncontradaException Se a chave primária do objeto não for encontrada ou configurada.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    void alterar(T entity) throws TipoChaveNaoEncontradaException, DAOException;

    /**
     * Consulta um registro no banco de dados com base em sua chave primária lógica.
     * @param valor A chave primária (identificador) do dado a ser consultado.
     * @return A entidade encontrada, ou {@code null} se não for encontrada.
     * @throws MaisDeUmRegistroException Se mais de um registro for encontrado para a chave.
     * @throws TableException Se houver um problema com o nome da tabela.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    T consultar(E valor) throws MaisDeUmRegistroException, TableException, DAOException;

    /**
     * Retorna todos os registros de uma determinada entidade/tabela no banco de dados.
     * @return Uma coleção de entidades encontradas. Pode ser vazia se não houver registros.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    Collection<T> buscarTodos() throws DAOException;
}