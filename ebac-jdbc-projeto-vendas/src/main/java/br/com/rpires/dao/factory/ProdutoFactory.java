package br.com.rpires.dao.factory;

import br.com.rpires.domain.Produto;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe utilitária responsável por converter um ResultSet (resultado de uma consulta SQL)
 * em um objeto Produto.
 */
public class ProdutoFactory {

    /**
     * Converte uma linha de um ResultSet em um objeto Produto.
     * Assume que o ResultSet contém as colunas necessárias para criar um Produto.
     * As colunas esperadas são: ID_PRODUTO (ou ID), CODIGO, NOME, DESCRICAO, VALOR.
     * @param rs O ResultSet contendo os dados do produto.
     * @return Um objeto Produto preenchido com os dados do ResultSet.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    public static Produto convert(ResultSet rs) throws SQLException {
        Produto prod = new Produto();
        // Mapeia as colunas do ResultSet para os atributos do objeto Produto
        // Note: rs.getLong("ID") é usado aqui se a query de venda não renomear para ID_PRODUTO.
        // Se a query usar "P.ID AS ID_PRODUTO", então rs.getLong("ID_PRODUTO") seria o correto.
        try {
            prod.setId(rs.getLong("ID_PRODUTO"));
        } catch (SQLException e) {
            // Fallback para "ID" se "ID_PRODUTO" não for encontrado (para GenericDAO.consultar/buscarTodos)
            prod.setId(rs.getLong("ID"));
        }
        prod.setCodigo(rs.getString("CODIGO"));
        prod.setNome(rs.getString("NOME"));
        prod.setDescricao(rs.getString("DESCRICAO"));
        prod.setValor(rs.getBigDecimal("VALOR"));
        return prod;
    }
}