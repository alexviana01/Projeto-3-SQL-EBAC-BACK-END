package br.com.rpires.dao;

import br.com.rpires.dao.generic.IGenericDAO;
import br.com.rpires.domain.Venda;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.TipoChaveNaoEncontradaException;

/**
 * Interface específica para o DAO de Venda.
 * Estende IGenericDAO e adiciona métodos específicos para o ciclo de vida de uma venda.
 */
public interface IVendaDAO extends IGenericDAO<Venda, String> {

    /**
     * Finaliza uma venda, atualizando seu status para CONCLUIDA.
     * @param venda A entidade Venda a ser finalizada.
     * @throws TipoChaveNaoEncontradaException Se a chave da venda não for encontrada.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    public void finalizarVenda(Venda venda) throws TipoChaveNaoEncontradaException, DAOException;

    /**
     * Cancela uma venda, atualizando seu status para CANCELADA.
     * @param venda A entidade Venda a ser cancelada.
     * @throws TipoChaveNaoEncontradaException Se a chave da venda não for encontrada.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    public void cancelarVenda(Venda venda) throws TipoChaveNaoEncontradaException, DAOException;
}