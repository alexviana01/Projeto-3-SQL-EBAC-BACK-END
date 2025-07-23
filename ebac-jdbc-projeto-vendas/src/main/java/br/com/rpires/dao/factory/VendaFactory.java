package br.com.rpires.dao.factory;

import br.com.rpires.domain.Cliente;
import br.com.rpires.domain.Venda;
import br.com.rpires.domain.Venda.Status;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe utilitária responsável por converter um ResultSet (resultado de uma consulta SQL)
 * em um objeto Venda.
 */
public class VendaFactory {

    /**
     * Converte uma linha de um ResultSet em um objeto Venda.
     * Assume que o ResultSet contém as colunas necessárias para criar uma Venda
     * e os dados do Cliente associado (normalmente via JOIN SQL).
     * As colunas esperadas incluem: ID_VENDA, CODIGO, VALOR_TOTAL, DATA_VENDA, STATUS_VENDA,
     * e todas as colunas necessárias para o Cliente (ID_CLIENTE, NOME, CPF, TEL, ENDERECO, NUMERO, CIDADE, ESTADO).
     * @param rs O ResultSet contendo os dados da venda e do cliente.
     * @return Um objeto Venda preenchido com os dados do ResultSet.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    public static Venda convert(ResultSet rs) throws SQLException {
        // Primeiro, cria o objeto Cliente usando o ClienteFactory, que já sabe mapear as colunas de cliente.
        Cliente cliente = ClienteFactory.convert(rs);

        Venda venda = new Venda();
        // Define o cliente associado à venda
        venda.setCliente(cliente);
        // Mapeia as colunas específicas de Venda
        venda.setId(rs.getLong("ID_VENDA"));
        venda.setCodigo(rs.getString("CODIGO"));
        venda.setValorTotal(rs.getBigDecimal("VALOR_TOTAL"));
        // Converte java.sql.Timestamp para java.time.Instant
        venda.setDataVenda(rs.getTimestamp("DATA_VENDA").toInstant());
        // Converte a string do status para o enum Status
        venda.setStatus(Status.getByName(rs.getString("STATUS_VENDA")));
        return venda;
    }
}