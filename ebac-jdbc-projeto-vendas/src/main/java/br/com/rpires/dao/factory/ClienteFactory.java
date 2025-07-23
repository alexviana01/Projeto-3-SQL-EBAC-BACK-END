package br.com.rpires.dao.factory;

import br.com.rpires.domain.Cliente;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe utilitária responsável por converter um ResultSet (resultado de uma consulta SQL)
 * em um objeto Cliente.
 */
public class ClienteFactory {

    /**
     * Converte uma linha de um ResultSet em um objeto Cliente.
     * Assume que o ResultSet contém as colunas necessárias para criar um Cliente.
     * As colunas esperadas são: ID_CLIENTE (ou ID), NOME, CPF, TEL, ENDERECO, NUMERO, CIDADE, ESTADO.
     * @param rs O ResultSet contendo os dados do cliente.
     * @return Um objeto Cliente preenchido com os dados do ResultSet.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    public static Cliente convert(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        // Mapeia as colunas do ResultSet para os atributos do objeto Cliente
        // Note: rs.getLong("ID") é usado aqui se a query de venda não renomear para ID_CLIENTE.
        // Se a query usar "C.ID AS ID_CLIENTE", então rs.getLong("ID_CLIENTE") seria o correto.
        // No contexto da VendaDAO, o SELECT renomeia para ID_CLIENTE. No GenericDAO, é apenas ID.
        try {
            cliente.setId(rs.getLong("ID_CLIENTE"));
        } catch (SQLException e) {
            // Fallback para "ID" se "ID_CLIENTE" não for encontrado (para GenericDAO.consultar/buscarTodos)
            cliente.setId(rs.getLong("ID"));
        }
        cliente.setNome(rs.getString("NOME"));
        cliente.setCpf(rs.getLong("CPF"));
        cliente.setTel(rs.getLong("TEL"));
        cliente.setEnd(rs.getString("ENDERECO"));
        cliente.setNumero(rs.getInt("NUMERO"));
        cliente.setCidade(rs.getString("CIDADE"));
        cliente.setEstado(rs.getString("ESTADO"));
        return cliente;
    }
}