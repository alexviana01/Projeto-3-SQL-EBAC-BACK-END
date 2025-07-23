package br.com.rpires.dao;

import br.com.rpires.dao.generic.IGenericDAO;
import br.com.rpires.domain.Cliente;

/**
 * Interface específica para o DAO de Cliente.
 * Estende IGenericDAO, herdando as operações CRUD básicas para a entidade Cliente.
 * Não adiciona métodos específicos, mas serve como um contrato claro para ClienteDAO.
 */
public interface IClienteDAO extends IGenericDAO<Cliente, Long> {
    // Métodos específicos para Cliente poderiam ser adicionados aqui, se necessário.
    // Por exemplo: Cliente findByEmail(String email);
}