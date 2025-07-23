package br.com.rpires.dao;

import br.com.rpires.dao.factory.ProdutoQuantidadeFactory;
import br.com.rpires.dao.factory.VendaFactory;
import br.com.rpires.dao.generic.GenericDAO;
import br.com.rpires.dao.generic.jdbc.ConnectionFactory;
import br.com.rpires.domain.ProdutoQuantidade;
import br.com.rpires.domain.Venda;
import br.com.rpires.domain.Venda.Status;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.MaisDeUmRegistroException;
import br.com.rpires.exceptions.TableException;
import br.com.rpires.exceptions.TipoChaveNaoEncontradaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementação do Data Access Object (DAO) para a entidade {@link Venda}.
 * Esta classe estende {@link GenericDAO} e implementa {@link IVendaDAO}, fornecendo
 * operações CRUD (Create, Read, Update, Delete) específicas para a entidade Venda.
 * Inclui lógica complexa para gerenciamento de vendas que envolvem múltiplos
 * {@link ProdutoQuantidade} (itens de venda), além de transações para garantir
 * a atomicidade das operações de cadastro e atualização de status.
 *
 * A exclusão de vendas via método genérico {@link #excluir(String)} não é permitida
 * para preservar o histórico de transações. Métodos específicos como
 * {@link #finalizarVenda(Venda)} e {@link #cancelarVenda(Venda)} devem ser usados
 * para alterar o status da venda.
 *
 * @author [Seu Nome]
 * @version 1.0
 * @see Venda
 * @see IVendaDAO
 * @see GenericDAO
 */
public class VendaDAO extends br.com.rpires.dao.generic.GenericDAO<br.com.rpires.domain.Venda, String> implements IVendaDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(VendaDAO.class);

    /**
     * Retorna a classe da entidade {@link Venda}.
     * Este método é utilizado pela classe genérica {@link GenericDAO} para
     * operações de reflexão e mapeamento.
     *
     * @return A classe {@code Venda.class}.
     */
    @Override
    public Class<Venda> getTipoClasse() {
        return Venda.class;
    }

    /**
     * Atualiza os dados de uma venda existente.
     * <p>
     * **Nota importante:** Para a entidade {@link Venda}, este método genérico
     * {@code atualizarDados} é limitado a atualizar apenas o código e o status da venda.
     * Operações de finalização ou cancelamento de vendas devem utilizar os
     * métodos específicos {@link #finalizarVenda(Venda)} e {@link #cancelarVenda(Venda)},
     * que tratam a lógica de negócio e as exceções apropriadas.
     * </p>
     *
     * @param entity           A entidade {@link Venda} contendo os novos dados a serem aplicados.
     * @param entityCadastrado A entidade {@link Venda} já existente no sistema, que será modificada.
     */
    @Override
    public void atualizarDados(br.com.rpires.domain.Venda entity, br.com.rpires.domain.Venda entityCadastrado) { // <--- DEVE ESTAR ASSIM
        entityCadastrado.setCodigo(entity.getCodigo());
        entityCadastrado.setStatus(entity.getStatus());
        // Não há atualização de produtos ou cliente aqui, pois é uma lógica complexa
        // que deve ser tratada em métodos de negócio específicos, se necessário.
    }

    /**
     * Lança {@link UnsupportedOperationException} indicando que a exclusão de vendas
     * não é permitida para manter a integridade do histórico de transações.
     * Em vez de excluir, as vendas devem ter seu status alterado para CANCELADA ou CONCLUIDA.
     *
     * @param valor O código da venda que se tentou excluir.
     * @throws UnsupportedOperationException Sempre lançada ao chamar este método.
     */
    @Override
    public void excluir(String valor) {
        LOGGER.warn("Tentativa de exclusão não permitida para Venda com código: {}", valor);
        throw new UnsupportedOperationException("OPERAÇÃO DE EXCLUSÃO NÃO PERMITIDA PARA VENDAS.");
    }

    /**
     * Finaliza uma venda, atualizando seu {@link Venda.Status} para {@code CONCLUIDA}
     * no banco de dados. Este método é transacional e garante a persistência do novo status.
     *
     * @param venda A entidade {@link Venda} a ser finalizada.
     * @throws DAOException Se ocorrer um erro SQL durante a atualização do status
     *                      ou problemas de conexão com o banco de dados.
     */
    @Override
    public void finalizarVenda(Venda venda) throws DAOException {
        Connection connection = null;
        PreparedStatement stm = null;
        try {
            String sql = "UPDATE TB_VENDA SET STATUS_VENDA = ? WHERE ID = ?";
            connection = ConnectionFactory.getConnection();
            stm = connection.prepareStatement(sql);
            stm.setString(1, Status.CONCLUIDA.name());
            stm.setLong(2, venda.getId());
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.warn("Nenhuma linha afetada ao tentar finalizar a venda com ID: {}. Venda pode não existir ou já estar finalizada.", venda.getId());
            } else {
                LOGGER.info("Venda com ID {} finalizada com sucesso. Status: CONCLUIDA.", venda.getId());
            }
        } catch (SQLException e) {
            LOGGER.error("Erro SQL ao finalizar venda {}: {}", venda.getCodigo(), e.getMessage(), e);
            throw new DAOException("ERRO AO FINALIZAR VENDA: " + venda.getCodigo() + ". Detalhes: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(connection, stm, null);
        }
    }

    /**
     * Cancela uma venda, atualizando seu {@link Venda.Status} para {@code CANCELADA}
     * no banco de dados. Este método é transacional e garante a persistência do novo status.
     *
     * @param venda A entidade {@link Venda} a ser cancelada.
     * @throws DAOException Se ocorrer um erro SQL durante a atualização do status
     *                      ou problemas de conexão com o banco de dados.
     */
    @Override
    public void cancelarVenda(Venda venda) throws DAOException {
        Connection connection = null;
        PreparedStatement stm = null;
        try {
            String sql = "UPDATE TB_VENDA SET STATUS_VENDA = ? WHERE ID = ?";
            connection = ConnectionFactory.getConnection();
            stm = connection.prepareStatement(sql);
            stm.setString(1, Status.CANCELADA.name());
            stm.setLong(2, venda.getId());
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.warn("Nenhuma linha afetada ao tentar cancelar a venda com ID: {}. Venda pode não existir ou já estar cancelada.", venda.getId());
            } else {
                LOGGER.info("Venda com ID {} cancelada com sucesso. Status: CANCELADA.", venda.getId());
            }
        } catch (SQLException e) {
            LOGGER.error("Erro SQL ao cancelar venda {}: {}", venda.getCodigo(), e.getMessage(), e);
            throw new DAOException("ERRO AO CANCELAR VENDA: " + venda.getCodigo() + ". Detalhes: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(connection, stm, null);
        }
    }

    /**
     * Retorna a string SQL para a operação de inserção de uma nova venda na tabela {@code TB_VENDA}.
     * A query utiliza a sequência {@code sq_venda} para gerar o ID técnico da venda.
     *
     * @return Uma string SQL formatada para inserção de dados em {@code TB_VENDA}.
     */
    @Override
    protected String getQueryInsercao() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO TB_VENDA ");
        sb.append("(ID, CODIGO, ID_CLIENTE_FK, VALOR_TOTAL, DATA_VENDA, STATUS_VENDA)");
        sb.append("VALUES (nextval('sq_venda'),?,?,?,?,?)");
        return sb.toString();
    }

    /**
     * Define os parâmetros para o {@link PreparedStatement} de inserção de uma {@link Venda}.
     * Os parâmetros são configurados na ordem em que aparecem na query gerada por {@link #getQueryInsercao()}.
     *
     * @param stmInsert O {@link PreparedStatement} preparado para a inserção.
     * @param entity    A entidade {@link Venda} contendo os dados a serem inseridos.
     * @throws SQLException Se ocorrer um erro ao definir os parâmetros no {@link PreparedStatement}.
     */
    @Override
    protected void setParametrosQueryInsercao(PreparedStatement stmInsert, Venda entity) throws SQLException {
        stmInsert.setString(1, entity.getCodigo());
        stmInsert.setLong(2, entity.getCliente().getId()); // ID do cliente (chave estrangeira)
        stmInsert.setBigDecimal(3, entity.getValorTotal());
        stmInsert.setTimestamp(4, Timestamp.from(entity.getDataVenda())); // Converte Instant para Timestamp
        stmInsert.setString(5, entity.getStatus().name()); // Nome do enum para o banco (String)
    }

    /**
     * Lança {@link UnsupportedOperationException}, pois a exclusão genérica de vendas
     * através de uma query direta não é permitida.
     *
     * @return Uma string SQL (este método nunca retorna um valor, pois sempre lança uma exceção).
     * @throws UnsupportedOperationException Sempre lançada ao chamar este método.
     */
    @Override
    protected String getQueryExclusao() {
        throw new UnsupportedOperationException("OPERAÇÃO DE EXCLUSÃO NÃO PERMITIDA PARA VENDAS.");
    }

    /**
     * Lança {@link UnsupportedOperationException}, pois a exclusão genérica de vendas
     * não é permitida.
     *
     * @param stmInsert O {@link PreparedStatement} para a exclusão (não utilizado).
     * @param valor     O valor da chave para exclusão (não utilizado).
     * @throws UnsupportedOperationException Sempre lançada ao chamar este método.
     * @throws SQLException                  (Não lançada diretamente por este método, mas exigido pela interface).
     */
    @Override
    protected void setParametrosQueryExclusao(PreparedStatement stmInsert, String valor) throws SQLException {
        throw new UnsupportedOperationException("OPERAÇÃO DE EXCLUSÃO NÃO PERMITIDA PARA VENDAS.");
    }

    /**
     * Lança {@link UnsupportedOperationException}, pois a atualização genérica de vendas
     * através de uma query direta não é permitida. A atualização de status deve ser feita
     * através de métodos específicos como {@link #finalizarVenda(Venda)} e {@link #cancelarVenda(Venda)}.
     *
     * @return Uma string SQL (este método nunca retorna um valor, pois sempre lança uma exceção).
     * @throws UnsupportedOperationException Sempre lançada ao chamar este método.
     */
    @Override
    protected String getQueryAtualizacao() {
        throw new UnsupportedOperationException("OPERAÇÃO DE ATUALIZAÇÃO GENÉRICA NÃO PERMITIDA PARA VENDAS.");
    }

    /**
     * Lança {@link UnsupportedOperationException}, pois a atualização genérica de vendas
     * não é permitida.
     *
     * @param stmUpdate O {@link PreparedStatement} para a atualização (não utilizado).
     * @param entity    A entidade {@link Venda} com os dados a serem atualizados (não utilizado).
     * @throws UnsupportedOperationException Sempre lançada ao chamar este método.
     * @throws SQLException                  (Não lançada diretamente por este método, mas exigido pela interface).
     */
    @Override
    protected void setParametrosQueryAtualizacao(PreparedStatement stmUpdate, Venda entity) throws SQLException {
        throw new UnsupportedOperationException("OPERAÇÃO DE ATUALIZAÇÃO GENÉRICA NÃO PERMITIDA PARA VENDAS.");
    }

    /**
     * Define os parâmetros para o {@link PreparedStatement} de seleção (consulta)
     * de uma {@link Venda} pelo seu código.
     *
     * @param stm   O {@link PreparedStatement} preparado para a consulta.
     * @param valor O código da venda a ser usado como critério de busca.
     * @throws SQLException Se ocorrer um erro ao definir o parâmetro no {@link PreparedStatement}.
     */
    @Override
    protected void setParametrosQuerySelect(PreparedStatement stm, String valor) throws SQLException {
        stm.setString(1, valor); // O valor é o código da Venda
    }

    /**
     * Consulta uma venda por seu código. Este método sobrescreve o método genérico
     * {@link GenericDAO#consultar(Serializable)} para incluir a busca dos itens de venda
     * ({@link ProdutoQuantidade}) e o {@link Cliente} associado à venda.
     * Realiza um JOIN para obter dados básicos da venda e do cliente, e em seguida,
     * busca os produtos e suas quantidades em uma chamada separada.
     *
     * @param valor O código da venda a ser consultada.
     * @return A entidade {@link Venda} completa, incluindo o cliente e todos os produtos,
     *         ou {@code null} se a venda não for encontrada.
     * @throws MaisDeUmRegistroException Se mais de uma venda for encontrada para o mesmo código,
     *                                   indicando um problema de unicidade de dados.
     * @throws TableException            Se houver um problema com o mapeamento da tabela.
     * @throws DAOException              Se ocorrer um erro de acesso a dados durante a consulta.
     */
    @Override
    public Venda consultar(String valor) throws MaisDeUmRegistroException, TableException, TipoChaveNaoEncontradaException, DAOException {
        StringBuilder sb = sqlBaseSelect();
        sb.append("WHERE V.CODIGO = ? ");
        Connection connection = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            validarMaisDeUmRegistro(valor); // Esta é a linha que lança a exceção TipoChaveNaoEncontradaException
            connection = ConnectionFactory.getConnection();
            stm = connection.prepareStatement(sb.toString());
            setParametrosQuerySelect(stm, valor);
            rs = stm.executeQuery();

            if (rs.next()) {
                Venda venda = VendaFactory.convert(rs);
                buscarAssociacaoVendaProdutos(connection, venda);
                return venda;
            }
        } catch (SQLException e) {
            LOGGER.error("Erro SQL ao consultar venda {}: {}", valor, e.getMessage(), e);
            throw new DAOException("ERRO CONSULTANDO VENDA: " + valor + ". Detalhes: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(connection, stm, rs);
        }
        return null;
    }


    /**
     * Busca os itens {@link ProdutoQuantidade} associados a uma {@link Venda} específica.
     * Este é um método auxiliar utilizado por {@link #consultar(String)} e {@link #buscarTodos()}.
     * Ele executa uma nova consulta ao banco de dados para recuperar todos os produtos
     * e suas quantidades que fazem parte da venda fornecida.
     *
     * @param connection A {@link Connection} JDBC ativa, passada do método chamador para reutilização.
     * @param venda      A entidade {@link Venda} para a qual buscar os produtos associados.
     * @throws DAOException Se ocorrer um erro de acesso a dados durante a consulta dos produtos da venda.
     */
    private void buscarAssociacaoVendaProdutos(Connection connection, Venda venda) throws DAOException {
        PreparedStatement stmProd = null;
        ResultSet rsProd = null;
        try {
            StringBuilder sbProd = new StringBuilder();
            // Query para buscar ProdutoQuantidade e seus Produtos associados via JOIN
            sbProd.append("SELECT PQ.ID, PQ.QUANTIDADE, PQ.VALOR_TOTAL, ");
            sbProd.append("P.ID AS ID_PRODUTO, P.CODIGO, P.NOME, P.DESCRICAO, P.VALOR ");
            sbProd.append("FROM TB_PRODUTO_QUANTIDADE PQ ");
            // Correção: A vírgula após PQ estava incorreta. A forma correta é usar INNER JOIN.
            sbProd.append("INNER JOIN TB_PRODUTO P ON P.ID = PQ.ID_PRODUTO_FK ");
            sbProd.append("WHERE PQ.ID_VENDA_FK = ?"); // Filtra pelos itens da venda atual

            stmProd = connection.prepareStatement(sbProd.toString());
            stmProd.setLong(1, venda.getId()); // Define o ID da venda como parâmetro
            rsProd = stmProd.executeQuery();

            Set<ProdutoQuantidade> produtos = new HashSet<>();
            while (rsProd.next()) { // Itera sobre os resultados dos itens
                ProdutoQuantidade prodQ = ProdutoQuantidadeFactory.convert(rsProd); // Converte para ProdutoQuantidade
                produtos.add(prodQ); // Adiciona ao conjunto
            }
            venda.setProdutos(produtos); // Define o conjunto de produtos na Venda
            venda.recalcularValorTotalVenda(); // Recalcula o valor total da venda para garantir consistência
            LOGGER.debug("Produtos para venda ID {} buscados com sucesso. Total: {}", venda.getId(), produtos.size());
        } catch (SQLException e) {
            LOGGER.error("Erro SQL ao consultar produtos da venda {}: {}", venda.getCodigo(), e.getMessage(), e);
            throw new DAOException("ERRO CONSULTANDO PRODUTOS DA VENDA: " + venda.getCodigo() + ". Detalhes: " + e.getMessage(), e);
        } finally {
            // Importante: Não fecha a conexão aqui, pois ela é gerenciada pelo método chamador.
            // Fecha apenas o PreparedStatement e ResultSet específicos desta operação.
            ConnectionFactory.closeConnection(null, stmProd, rsProd);
        }
    }

    /**
     * Retorna todas as vendas cadastradas no sistema. Para cada venda, são buscados
     * os detalhes do cliente associado e todos os itens de produto ({@link ProdutoQuantidade})
     * que compõem a venda.
     *
     * @return Uma {@link Collection} de objetos {@link Venda} completos.
     *         A coleção pode estar vazia se não houver vendas cadastradas.
     * @throws DAOException Se ocorrer um erro de acesso a dados durante a busca das vendas.
     */
    @Override
    public Collection<Venda> buscarTodos() throws DAOException {
        List<Venda> lista = new ArrayList<>();
        StringBuilder sb = sqlBaseSelect(); // Constrói a query SELECT principal (Venda + Cliente)

        Connection connection = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            connection = ConnectionFactory.getConnection();
            stm = connection.prepareStatement(sb.toString());
            rs = stm.executeQuery(); // Executa a consulta

            while (rs.next()) { // Itera sobre cada venda encontrada
                Venda venda = VendaFactory.convert(rs); // Converte o ResultSet para o objeto Venda (já com o Cliente)
                // Busca os itens de ProdutoQuantidade para esta venda em uma consulta separada
                buscarAssociacaoVendaProdutos(connection, venda);
                lista.add(venda); // Adiciona a venda completa à lista
            }
            LOGGER.info("Total de {} vendas encontradas no sistema.", lista.size());
        } catch (SQLException e) {
            LOGGER.error("Erro SQL ao buscar todas as vendas: {}", e.getMessage(), e);
            throw new DAOException("ERRO BUSCANDO TODAS AS VENDAS. Detalhes: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(connection, stm, rs); // Fecha os recursos do banco de dados
        }
        return lista;
    }

    /**
     * Constrói a parte base da query SELECT para a entidade {@link Venda},
     * incluindo um {@code INNER JOIN} com a tabela {@code TB_CLIENTE} para obter
     * os dados do cliente associado à venda em uma única consulta inicial.
     *
     * @return Um {@link StringBuilder} contendo a porção inicial da query SQL.
     */
    private StringBuilder sqlBaseSelect() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT V.ID AS ID_VENDA, V.CODIGO, V.VALOR_TOTAL, V.DATA_VENDA, V.STATUS_VENDA, ");
        sb.append("C.ID AS ID_CLIENTE, C.NOME, C.CPF, C.TEL, C.ENDERECO, C.NUMERO, C.CIDADE, C.ESTADO ");
        sb.append("FROM TB_VENDA V ");
        sb.append("INNER JOIN TB_CLIENTE C ON V.ID_CLIENTE_FK = C.ID "); // Junta com a tabela de clientes
        return sb;
    }

    /**
     * Cadastra uma nova {@link Venda} no banco de dados, incluindo seus itens
     * ({@link ProdutoQuantidade}). Esta operação é realizada dentro de uma transação
     * para garantir que tanto a venda quanto todos os seus itens sejam persistidos
     * de forma atômica. Se a inserção de qualquer item falhar, toda a transação é revertida.
     *
     * @param entity A entidade {@link Venda} a ser cadastrada.
     * @return {@code true} se o cadastro da venda e de todos os seus itens for bem-sucedido,
     *         {@code false} caso a inserção da venda principal falhe.
     * @throws TipoChaveNaoEncontradaException Se a chave da venda não for encontrada ou configurada.
     * @throws DAOException              Se ocorrer um erro de acesso a dados durante a transação,
     *                                   incluindo erros SQL ou problemas de conexão.
     */
    @Override
    public Boolean cadastrar(Venda entity) throws TipoChaveNaoEncontradaException, DAOException {
        Connection connection = null;
        PreparedStatement stm = null;
        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false); // Inicia a transação: desabilita o auto-commit
            // Prepara a inserção da Venda e solicita o ID gerado pelo banco de dados
            stm = connection.prepareStatement(getQueryInsercao(), Statement.RETURN_GENERATED_KEYS);
            setParametrosQueryInsercao(stm, entity); // Define parâmetros da Venda
            int rowsAffected = stm.executeUpdate(); // Executa inserção da Venda

            if (rowsAffected > 0) { // Se a venda principal foi inserida com sucesso
                try (ResultSet rs = stm.getGeneratedKeys()) { // Obtém o ID gerado para a Venda
                    if (rs.next()) {
                        entity.setId(rs.getLong(1)); // Define o ID da Venda no objeto
                    }
                }

                // Itera sobre cada item de ProdutoQuantidade da Venda para inseri-lo
                for (ProdutoQuantidade prod : entity.getProdutos()) {
                    stm = connection.prepareStatement(getQueryInsercaoProdQuant()); // Prepara inserção do ProdutoQuantidade
                    setParametrosQueryInsercaoProdQuant(stm, entity, prod); // Define parâmetros do ProdutoQuantidade
                    rowsAffected = stm.executeUpdate(); // Executa inserção do item
                    if (rowsAffected == 0) {
                        connection.rollback(); // Se um item falhar, faz rollback de toda a transação
                        LOGGER.error("Falha ao inserir item de produto (código: {}) para venda {}. Rollback da transação.", prod.getProduto().getCodigo(), entity.getCodigo());
                        return false; // Retorna false, indicando falha
                    }
                }

                connection.commit(); // Confirma a transação se tudo deu certo (venda e todos os itens)
                LOGGER.info("Venda {} cadastrada com sucesso, incluindo {} itens.", entity.getCodigo(), entity.getProdutos().size());
                return true;
            } else {
                connection.rollback(); // Se a venda principal não foi inserida, faz rollback
                LOGGER.error("Falha ao inserir a venda {}. Nenhuma linha afetada. Rollback da transação.", entity.getCodigo());
                return false;
            }

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Em caso de erro SQL, faz rollback da transação
                }
            } catch (SQLException ex) {
                LOGGER.error("Erro ao realizar rollback após exceção SQL: {}", ex.getMessage(), ex);
            }
            LOGGER.error("Erro SQL ao cadastrar venda {}: {}. Detalhes: {}", entity.getCodigo(), e.getMessage(), e);
            throw new DAOException("ERRO CADASTRANDO VENDA: " + entity.getCodigo() + ". Detalhes: " + e.getMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restaura o auto-commit para o estado padrão
                }
            } catch (SQLException ex) {
                LOGGER.error("Erro ao restaurar auto-commit para a conexão: {}", ex.getMessage(), ex);
            }
            ConnectionFactory.closeConnection(connection, stm, null); // Fecha os recursos do banco de dados
        }
    }

    /**
     * Retorna a string SQL para a operação de inserção de um item
     * {@link ProdutoQuantidade} na tabela {@code TB_PRODUTO_QUANTIDADE}.
     * A query utiliza a sequência {@code sq_produto_quantidade} para gerar o ID técnico do item.
     *
     * @return Uma string SQL formatada para inserção de {@link ProdutoQuantidade}.
     */
    private String getQueryInsercaoProdQuant() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO TB_PRODUTO_QUANTIDADE ");
        sb.append("(ID, ID_PRODUTO_FK, ID_VENDA_FK, QUANTIDADE, VALOR_TOTAL)");
        sb.append("VALUES (nextval('sq_produto_quantidade'),?,?,?,?)");
        return sb.toString();
    }

    /**
     * Define os parâmetros para o {@link PreparedStatement} de inserção de um item
     * {@link ProdutoQuantidade}. Os parâmetros são configurados na ordem em que aparecem
     * na query gerada por {@link #getQueryInsercaoProdQuant()}.
     *
     * @param stm   O {@link PreparedStatement} preparado para a inserção.
     * @param venda A entidade {@link Venda} à qual o item pertence. O ID da venda é usado como chave estrangeira.
     * @param prod  O item {@link ProdutoQuantidade} a ser inserido.
     * @throws SQLException Se ocorrer um erro ao definir os parâmetros no {@link PreparedStatement}.
     */
    private void setParametrosQueryInsercaoProdQuant(PreparedStatement stm, Venda venda, ProdutoQuantidade prod) throws SQLException {
        stm.setLong(1, prod.getProduto().getId()); // ID do Produto (chave estrangeira)
        stm.setLong(2, venda.getId()); // ID da Venda (chave estrangeira)
        stm.setInt(3, prod.getQuantidade());
        stm.setBigDecimal(4, prod.getValorTotal());
    }
}