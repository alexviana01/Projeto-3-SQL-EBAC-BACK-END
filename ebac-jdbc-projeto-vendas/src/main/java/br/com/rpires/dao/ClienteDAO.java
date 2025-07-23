package br.com.rpires.dao;

import br.com.rpires.dao.generic.GenericDAO;
import br.com.rpires.domain.Cliente;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementação do DAO para a entidade Cliente.
 * Estende GenericDAO e implementa IClienteDAO.
 * Fornece as queries SQL específicas e os métodos para setar parâmetros para Cliente.
 */
public class ClienteDAO extends br.com.rpires.dao.generic.GenericDAO<br.com.rpires.domain.Cliente, Long> implements IClienteDAO {

    public ClienteDAO() {
        super();
    }

    /**
     * Retorna a classe da entidade Cliente.
     * Necessário para a reflexão na classe genérica.
     * @return A classe Cliente.
     */
    @Override
    public Class<Cliente> getTipoClasse() {
        return Cliente.class;
    }

    /**
     * Atualiza os dados de um cliente.
     * Este método é chamado pela camada genérica após a consulta da entidade a ser alterada.
     * Para a entidade Cliente, atualizamos todos os campos, exceto o CPF (que é a chave lógica).
     * @param entity A entidade Cliente com os novos dados.
     * @param entityCadastrado A entidade Cliente já existente no sistema a ser atualizada.
     */
    @Override
    public void atualizarDados(br.com.rpires.domain.Cliente entity, br.com.rpires.domain.Cliente entityCadastrado) { 
        entityCadastrado.setCidade(entity.getCidade());
        // entityCadastrado.setCpf(entity.getCpf()); // CPF não deve ser atualizado, é chave lógica
        entityCadastrado.setEnd(entity.getEnd());
        entityCadastrado.setEstado(entity.getEstado());
        entityCadastrado.setNome(entity.getNome());
        entityCadastrado.setNumero(entity.getNumero());
        entityCadastrado.setTel(entity.getTel());
    }

    /**
     * Retorna a query SQL para inserir um novo cliente.
     * Usa uma sequência (nextval) para gerar o ID técnico.
     * @return A string SQL para inserção.
     */
    @Override
    protected String getQueryInsercao() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO TB_CLIENTE ");
        sb.append("(ID, NOME, CPF, TEL, ENDERECO, NUMERO, CIDADE, ESTADO)");
        sb.append("VALUES (nextval('sq_cliente'),?,?,?,?,?,?,?)");
        return sb.toString();
    }

    /**
     * Define os parâmetros para a query de inserção.
     * A ordem dos 'set' deve corresponder à ordem dos '?' na query.
     * @param stmInsert O PreparedStatement para a inserção.
     * @param entity A entidade Cliente com os dados a serem inseridos.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    @Override
    protected void setParametrosQueryInsercao(PreparedStatement stmInsert, Cliente entity) throws SQLException {
        stmInsert.setString(1, entity.getNome());
        stmInsert.setLong(2, entity.getCpf());
        stmInsert.setLong(3, entity.getTel());
        stmInsert.setString(4, entity.getEnd());
        stmInsert.setLong(5, entity.getNumero());
        stmInsert.setString(6, entity.getCidade());
        stmInsert.setString(7, entity.getEstado());
    }

    /**
     * Retorna a query SQL para excluir um cliente pelo CPF (chave lógica).
     * @return A string SQL para exclusão.
     */
    @Override
    protected String getQueryExclusao() {
        return "DELETE FROM TB_CLIENTE WHERE CPF = ?";
    }

    /**
     * Define os parâmetros para a query de exclusão.
     * @param stmExclusao O PreparedStatement para a exclusão.
     * @param valor O valor do CPF a ser usado como critério de exclusão.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    @Override
    protected void setParametrosQueryExclusao(PreparedStatement stmExclusao, Long valor) throws SQLException {
        stmExclusao.setLong(1, valor); // Valor aqui é o CPF
    }

    /**
     * Retorna a query SQL para atualizar um cliente pelo CPF (chave lógica).
     * @return A string SQL para atualização.
     */
    @Override
    protected String getQueryAtualizacao() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE TB_CLIENTE ");
        sb.append("SET NOME = ?,");
        sb.append("TEL = ?,");
        sb.append("ENDERECO = ?,");
        sb.append("NUMERO = ?,");
        sb.append("CIDADE = ?,");
        sb.append("ESTADO = ? ");
        sb.append("WHERE CPF = ? "); // O CPF é usado como critério para WHERE
        return sb.toString();
    }

    /**
     * Define os parâmetros para a query de atualização.
     * O último parâmetro deve ser o CPF, que é o critério de busca na cláusula WHERE.
     * @param stmUpdate O PreparedStatement para a atualização.
     * @param entity A entidade Cliente com os novos dados e o CPF para o critério.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    @Override
    protected void setParametrosQueryAtualizacao(PreparedStatement stmUpdate, Cliente entity) throws SQLException {
        stmUpdate.setString(1, entity.getNome());
        stmUpdate.setLong(2, entity.getTel());
        stmUpdate.setString(3, entity.getEnd());
        stmUpdate.setLong(4, entity.getNumero());
        stmUpdate.setString(5, entity.getCidade());
        stmUpdate.setString(6, entity.getEstado());
        stmUpdate.setLong(7, entity.getCpf()); // CPF como o último parâmetro para a cláusula WHERE
    }

    /**
     * Define os parâmetros para a query de seleção (consulta) pelo CPF.
     * @param stmSelect O PreparedStatement para a consulta.
     * @param valor O valor do CPF a ser usado como critério de consulta.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    @Override
    protected void setParametrosQuerySelect(PreparedStatement stmSelect, Long valor) throws SQLException {
        stmSelect.setLong(1, valor); // Valor aqui é o CPF
    }
}