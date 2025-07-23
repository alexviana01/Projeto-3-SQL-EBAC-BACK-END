package br.com.rpires.dao.factory;

import br.com.rpires.domain.Produto;
import br.com.rpires.domain.ProdutoQuantidade;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe utilitária responsável por converter um ResultSet (resultado de uma consulta SQL)
 * em um objeto ProdutoQuantidade.
 */
public class ProdutoQuantidadeFactory {

    /**
     * Converte uma linha de um ResultSet em um objeto ProdutoQuantidade.
     * Assume que o ResultSet contém as colunas necessárias para criar um ProdutoQuantidade
     * e os dados do Produto associado.
     * As colunas esperadas incluem: ID (do ProdutoQuantidade na tabela N:M), QUANTIDADE, VALOR_TOTAL,
     * e todas as colunas necessárias para o Produto (ID_PRODUTO, CODIGO, NOME, DESCRICAO, VALOR).
     * @param rs O ResultSet contendo os dados do item de produto e do produto.
     * @return Um objeto ProdutoQuantidade preenchido com os dados do ResultSet.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    public static ProdutoQuantidade convert(ResultSet rs) throws SQLException {
        // Primeiro, cria o objeto Produto usando o ProdutoFactory, que já sabe mapear as colunas de produto.
        // Assume que o ResultSet contém as colunas do Produto prefixadas ou com aliases específicos.
        Produto prod = ProdutoFactory.convert(rs);

        ProdutoQuantidade prodQ = new ProdutoQuantidade();
        // Define o produto associado ao item de quantidade
        prodQ.setProduto(prod);
        // Mapeia as colunas específicas de ProdutoQuantidade
        prodQ.setId(rs.getLong("ID")); // ID do item de ProdutoQuantidade na tabela N:M
        prodQ.setQuantidade(rs.getInt("QUANTIDADE"));
        prodQ.setValorTotal(rs.getBigDecimal("VALOR_TOTAL"));
        return prodQ;
    }
}