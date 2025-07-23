package br.com.rpires.dao;

import br.com.rpires.dao.generic.GenericDAO;
import br.com.rpires.domain.Produto;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementação do DAO para a entidade Produto.
 * Estende GenericDAO e implementa IProdutoDAO.
 * Fornece as queries SQL específicas e os métodos para setar parâmetros para Produto.
 */
public class ProdutoDAO extends GenericDAO<Produto, String> implements IProdutoDAO {

    public ProdutoDAO() {
        super();
    }

    /**
     * Retorna a classe da entidade Produto.
     * Necessário para a reflexão na classe genérica.
     * @return A classe Produto.
     */
    @Override
    public Class<Produto> getTipoClasse() {
        return Produto.class;
    }

    /**
     * Atualiza os dados de um produto.
     * Para a entidade Produto, atualizamos todos os campos, exceto o Código (que é a chave lógica).
     * @param entity A entidade Produto com os novos dados.
     * @param entityCadastrado A entidade Produto já existente no sistema a ser atualizada.
     */
    @Override
    public void atualizarDados(Produto entity, Produto entityCadastrado) {
        // entityCadastrado.setCodigo(entity.getCodigo()); // Código não deve ser atualizado, é chave lógica
        entityCadastrado.setDescricao(entity.getDescricao());
        entityCadastrado.setNome(entity.getNome());
        entityCadastrado.setValor(entity.getValor());
    }

    /**
     * Retorna a query SQL para inserir um novo produto.
     * Usa uma sequência (nextval) para gerar o ID técnico.
     * @return A string SQL para inserção.
     */
    @Override
    protected String getQueryInsercao() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO TB_PRODUTO ");
        sb.append("(ID, CODIGO, NOME, DESCRICAO, VALOR)");
        sb.append("VALUES (nextval('sq_produto'),?,?,?,?)");
        return sb.toString();
    }

    /**
     * Define os parâmetros para a query de inserção.
     * A ordem dos 'set' deve corresponder à ordem dos '?' na query.
     * @param stmInsert O PreparedStatement para a inserção.
     * @param entity A entidade Produto com os dados a serem inseridos.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    @Override
    protected void setParametrosQueryInsercao(PreparedStatement stmInsert, Produto entity) throws SQLException {
        stmInsert.setString(1, entity.getCodigo());
        stmInsert.setString(2, entity.getNome());
        stmInsert.setString(3, entity.getDescricao());
        stmInsert.setBigDecimal(4, entity.getValor());
    }

    /**
     * Retorna a query SQL para excluir um produto pelo Código (chave lógica).
     * @return A string SQL para exclusão.
     */
    @Override
    protected String getQueryExclusao() {
        return "DELETE FROM TB_PRODUTO WHERE CODIGO = ?";
    }

    /**
     * Define os parâmetros para a query de exclusão.
     * @param stmExclusao O PreparedStatement para a exclusão.
     * @param valor O valor do Código a ser usado como critério de exclusão.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    @Override
    protected void setParametrosQueryExclusao(PreparedStatement stmExclusao, String valor) throws SQLException {
        stmExclusao.setString(1, valor); // Valor aqui é o Código
    }

    /**
     * Retorna a query SQL para atualizar um produto pelo Código (chave lógica).
     * @return A string SQL para atualização.
     */
    @Override
    protected String getQueryAtualizacao() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE TB_PRODUTO ");
        sb.append("SET NOME = ?,");
        sb.append("DESCRICAO = ?,");
        sb.append("VALOR = ? ");
        sb.append("WHERE CODIGO = ? "); // O CODIGO é usado como critério para WHERE
        return sb.toString();
    }

    /**
     * Define os parâmetros para a query de atualização.
     * O último parâmetro deve ser o Código, que é o critério de busca na cláusula WHERE.
     * @param stmUpdate O PreparedStatement para a atualização.
     * @param entity A entidade Produto com os novos dados e o Código para o critério.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    @Override
    protected void setParametrosQueryAtualizacao(PreparedStatement stmUpdate, Produto entity) throws SQLException {
        stmUpdate.setString(1, entity.getNome());
        stmUpdate.setString(2, entity.getDescricao());
        stmUpdate.setBigDecimal(3, entity.getValor());
        stmUpdate.setString(4, entity.getCodigo()); // CODIGO como o último parâmetro para a cláusula WHERE
    }

    /**
     * Define os parâmetros para a query de seleção (consulta) pelo Código.
     * @param stmSelect O PreparedStatement para a consulta.
     * @param valor O valor do Código a ser usado como critério de consulta.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    @Override
    protected void setParametrosQuerySelect(PreparedStatement stmSelect, String valor) throws SQLException {
        stmSelect.setString(1, valor); // Valor aqui é o Código
    }
}