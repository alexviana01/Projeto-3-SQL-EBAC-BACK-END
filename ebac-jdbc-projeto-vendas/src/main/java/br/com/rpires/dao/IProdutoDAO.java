package br.com.rpires.dao;

import br.com.rpires.dao.generic.IGenericDAO;
import br.com.rpires.domain.Produto;

/**
 * Interface específica para o DAO de Produto.
 * Estende IGenericDAO, herdando as operações CRUD básicas para a entidade Produto.
 */
public interface IProdutoDAO extends IGenericDAO<Produto, String>{
    // Métodos específicos para Produto poderiam ser adicionados aqui.
}