package br.com.rpires.dao.generic;

//Linha CORRETA
import br.com.rpires.domain.Persistente;

import br.com.rpires.dao.generic.jdbc.ConnectionFactory;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.MaisDeUmRegistroException;
import br.com.rpires.exceptions.TableException;
import br.com.rpires.exceptions.TipoChaveNaoEncontradaException;
import br.com.rpires.exceptions.TipoElementoNaoConhecidoException;
import br.com.rpires.infra.annotations.ColunaTabela;
import br.com.rpires.infra.annotations.Tabela;
import br.com.rpires.infra.annotations.TipoChave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Classe genérica que implementa a interface IGenericDAO, fornecendo as operações CRUD.
 * Utiliza anotações e reflexão para mapear objetos Java para o banco de dados
 * e vice-versa, sem a necessidade de um ORM completo.
 *
 * Esta classe segue o padrão Template Method, delegando a construção de queries
 * e o set de parâmetros para as subclasses concretas.
 *
 * @param <T> O tipo da entidade persistível.
 * @param <E> O tipo da chave primária (identificador) da entidade.
 */
public abstract class GenericDAO<T extends br.com.rpires.domain.Persistente, E extends java.io.Serializable> implements IGenericDAO<T, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericDAO.class);

    // Métodos abstratos que devem ser implementados pelas subclasses (DAOs específicos)
    // Estes métodos formam os "ganchos" do padrão Template Method.

    /**
     * Retorna a classe da entidade T. Usado para reflexão.
     * Ex: return Cliente.class;
     * @return A classe da entidade.
     */
    public abstract Class<T> getTipoClasse();

    /**
     * Método para atualizar os dados de uma entidade.
     * Recebe a entidade com os dados novos e a entidade já cadastrada para atualização.
     * Implementado pelas subclasses para definir quais campos podem ser atualizados.
     * @param entity A entidade com os dados novos.
     * @param entityCadastrado A entidade já cadastrada para atualização.
     */
    public abstract void atualizarDados(T entity, T entityCadastrado);

    /**
     * Retorna a string SQL para a operação de inserção.
     * Ex: "INSERT INTO TB_CLIENTE (ID, NOME, CPF) VALUES (nextval('sq_cliente'), ?, ?)"
     * @return A string SQL de inserção.
     */
    protected abstract String getQueryInsercao();

    /**
     * Retorna a string SQL para a operação de exclusão.
     * Ex: "DELETE FROM TB_CLIENTE WHERE CPF = ?"
     * @return A string SQL de exclusão.
     */
    protected abstract String getQueryExclusao();

    /**
     * Retorna a string SQL para a operação de atualização.
     * Ex: "UPDATE TB_CLIENTE SET NOME = ?, TEL = ? WHERE CPF = ?"
     * @return A string SQL de atualização.
     */
    protected abstract String getQueryAtualizacao();

    /**
     * Define os parâmetros para a PreparedStatement de inserção.
     * @param stmInsert O PreparedStatement de inserção.
     * @param entity A entidade com os valores a serem inseridos.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    protected abstract void setParametrosQueryInsercao(PreparedStatement stmInsert, T entity) throws SQLException;

    /**
     * Define os parâmetros para a PreparedStatement de exclusão.
     * @param stmDelete O PreparedStatement de exclusão.
     * @param valor O valor da chave a ser usada na exclusão.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    protected abstract void setParametrosQueryExclusao(PreparedStatement stmDelete, E valor) throws SQLException;

    /**
     * Define os parâmetros para a PreparedStatement de atualização.
     * @param stmUpdate O PreparedStatement de atualização.
     * @param entity A entidade com os valores a serem atualizados e o critério.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    protected abstract void setParametrosQueryAtualizacao(PreparedStatement stmUpdate, T entity) throws SQLException;

    /**
     * Define os parâmetros para a PreparedStatement de consulta (SELECT).
     * @param stmSelect O PreparedStatement de consulta.
     * @param valor O valor da chave a ser usada na consulta.
     * @throws SQLException Se ocorrer um erro SQL.
     */
    protected abstract void setParametrosQuerySelect(PreparedStatement stmSelect, E valor) throws SQLException;

    // Métodos concretos que implementam o IGenericDAO

    /**
     * Obtém o valor da chave lógica (identificador principal) de uma entidade
     * usando a anotação @TipoChave e reflexão.
     * @param entity A entidade da qual extrair a chave.
     * @return O valor da chave.
     * @throws TipoChaveNaoEncontradaException Se a anotação @TipoChave não for encontrada
     *                                          ou o método getter não puder ser invocado.
     */
    public E getChave(T entity) throws TipoChaveNaoEncontradaException {
        // Percorre todos os campos declarados na classe da entidade
        Field[] fields = entity.getClass().getDeclaredFields();
        E returnValue = null; // Variável para armazenar o valor da chave
        for (Field field : fields) {
            // Verifica se o campo possui a anotação @TipoChave
            if (field.isAnnotationPresent(TipoChave.class)) {
                // Obtém a anotação e o nome do método getter especificado
                TipoChave tipoChave = field.getAnnotation(TipoChave.class);
                String nomeMetodo = tipoChave.value();
                try {
                    // Obtém o objeto Method correspondente ao nome do método getter
                    Method method = entity.getClass().getMethod(nomeMetodo);
                    // Invoca o método getter na entidade para obter o valor da chave
                    returnValue = (E) method.invoke(entity);
                    LOGGER.debug("Chave '{}' encontrada para a entidade {}", returnValue, entity.getClass().getSimpleName());
                    return returnValue; // Retorna o valor da chave encontrado
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    // Captura e lança uma exceção de negócio se houver problemas na reflexão
                    LOGGER.error("Erro ao invocar método getter para a chave principal da entidade {}. Método: {}. Detalhes: {}", entity.getClass().getSimpleName(), nomeMetodo, e.getMessage(), e);
                    throw new TipoChaveNaoEncontradaException("Chave principal do objeto " + entity.getClass().getSimpleName() + " não encontrada ou método getter inválido.", e);
                }
            }
        }
        // Se nenhum campo com @TipoChave foi encontrado, lança uma exceção
        String msg = "Chave principal do objeto " + entity.getClass().getSimpleName() + " não encontrada. Verifique a anotação @TipoChave.";
        LOGGER.error(msg);
        throw new TipoChaveNaoEncontradaException(msg);
    }

    /**
     * Cadastra um novo registro no banco de dados.
     * @param entity A entidade a ser cadastrada.
     * @return {@code true} se o cadastro foi bem-sucedido, {@code false} caso contrário.
     * @throws TipoChaveNaoEncontradaException Se a chave primária do objeto não for encontrada ou configurada.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    @Override
    public Boolean cadastrar(T entity) throws TipoChaveNaoEncontradaException, DAOException {
        Connection connection = null;
        PreparedStatement stm = null;
        try {
            connection = ConnectionFactory.getConnection(); // Obtém a conexão
            stm = connection.prepareStatement(getQueryInsercao(), Statement.RETURN_GENERATED_KEYS); // Prepara a instrução SQL, solicitando as chaves geradas
            setParametrosQueryInsercao(stm, entity); // Define os parâmetros da query
            int rowsAffected = stm.executeUpdate(); // Executa a atualização (inserção)

            if (rowsAffected > 0) { // Se alguma linha foi afetada (inserção bem-sucedida)
                try (ResultSet rs = stm.getGeneratedKeys()) { // Tenta obter as chaves geradas
                    if (rs.next()) { // Se houver uma chave gerada
                        Persistente per = (Persistente) entity;
                        per.setId(rs.getLong(1)); // Define o ID gerado na entidade
                        LOGGER.info("Entidade {} cadastrada com sucesso. ID gerado: {}", entity.getClass().getSimpleName(), per.getId());
                    }
                }
                return true; // Retorna verdadeiro indicando sucesso
            }
            LOGGER.warn("Nenhuma linha afetada ao tentar cadastrar a entidade {}.", entity.getClass().getSimpleName());
            return false; // Retorna falso se nenhuma linha foi afetada

        } catch (SQLException e) {
            // Em caso de erro SQL, encapsula em DAOException e relança
            LOGGER.error("Erro SQL ao cadastrar objeto {}: {}", entity.getClass().getSimpleName(), e.getMessage(), e);
            throw new DAOException("ERRO CADASTRANDO OBJETO: " + entity.getClass().getSimpleName() + ". Detalhes: " + e.getMessage(), e);
        } finally {
            // Garante que a conexão e o statement sejam fechados
            ConnectionFactory.closeConnection(connection, stm, null);
        }
    }

    /**
     * Exclui um registro do banco de dados com base em sua chave primária lógica.
     * @param valor A chave primária (identificador) do dado a ser excluído.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    @Override
    public void excluir(E valor) throws DAOException {
        Connection connection = null;
        PreparedStatement stm = null;
        try {
            connection = ConnectionFactory.getConnection(); // Obtém a conexão
            stm = connection.prepareStatement(getQueryExclusao()); // Prepara a instrução SQL de exclusão
            setParametrosQueryExclusao(stm, valor); // Define os parâmetros da query (o valor da chave para exclusão)
            int rowsAffected = stm.executeUpdate(); // Executa a exclusão
            if (rowsAffected > 0) {
                LOGGER.info("Entidade com chave {} excluída com sucesso.", valor);
            } else {
                LOGGER.warn("Nenhuma linha afetada ao tentar excluir a entidade com chave {}. Pode não existir.", valor);
            }

        } catch (SQLException e) {
            LOGGER.error("Erro SQL ao excluir objeto com chave {}: {}", valor, e.getMessage(), e);
            throw new DAOException("ERRO EXCLUINDO OBJETO. Detalhes: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(connection, stm, null); // Fecha recursos
        }
    }

    /**
     * Altera um registro existente no banco de dados.
     * @param entity A entidade com os dados atualizados.
     * @throws TipoChaveNaoEncontradaException Se a chave primária do objeto não for encontrada ou configurada.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    @Override
    public void alterar(T entity) throws TipoChaveNaoEncontradaException, DAOException {
        Connection connection = null;
        PreparedStatement stm = null;
        try {
            connection = ConnectionFactory.getConnection(); // Obtém a conexão
            stm = connection.prepareStatement(getQueryAtualizacao()); // Prepara a instrução SQL de atualização
            setParametrosQueryAtualizacao(stm, entity); // Define os parâmetros da query (valores a serem atualizados e critério)
            int rowsAffected = stm.executeUpdate(); // Executa a atualização
            if (rowsAffected > 0) {
                LOGGER.info("Entidade {} com chave {} alterada com sucesso.", entity.getClass().getSimpleName(), getChave(entity));
            } else {
                LOGGER.warn("Nenhuma linha afetada ao tentar alterar a entidade {} com chave {}. Pode não existir.", entity.getClass().getSimpleName(), getChave(entity));
            }
        } catch (SQLException e) {
            LOGGER.error("Erro SQL ao alterar objeto {}: {}", entity.getClass().getSimpleName(), e.getMessage(), e);
            throw new DAOException("ERRO ALTERANDO OBJETO: " + entity.getClass().getSimpleName() + ". Detalhes: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(connection, stm, null); // Fecha recursos
        }
    }

    /**
     * Consulta um registro no banco de dados com base em sua chave primária lógica.
     * @param valor A chave primária (identificador) do dado a ser consultado.
     * @return A entidade encontrada, ou {@code null} se não for encontrada.
     * @throws MaisDeUmRegistroException Se mais de um registro for encontrado para a chave.
     * @throws TableException Se houver um problema com o nome da tabela.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    @Override
    public T consultar(E valor) throws MaisDeUmRegistroException, TableException, DAOException {
        Connection connection = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            validarMaisDeUmRegistro(valor); // Verifica se há mais de um registro para a chave
            connection = ConnectionFactory.getConnection(); // Obtém a conexão
            // Constrói a query SELECT dinamicamente com base na anotação @Tabela e @TipoChave
            String tableName = getTableName();
            String keyFieldName = getNomeCampoChave(getTipoClasse());
            String sql = "SELECT * FROM " + tableName + " WHERE " + keyFieldName + " = ?";
            stm = connection.prepareStatement(sql);
            setParametrosQuerySelect(stm, valor); // Define o parâmetro da query (o valor da chave para consulta)
            rs = stm.executeQuery(); // Executa a consulta

            if (rs.next()) { // Se um registro for encontrado
                // Cria uma nova instância da entidade usando o construtor padrão (via reflexão)
                T entity = getTipoClasse().getConstructor().newInstance();
                // Itera sobre os campos da entidade para preencher seus valores
                for (Field field : entity.getClass().getDeclaredFields()) {
                    // Se o campo tiver a anotação @ColunaTabela
                    if (field.isAnnotationPresent(ColunaTabela.class)) {
                        ColunaTabela coluna = field.getAnnotation(ColunaTabela.class);
                        String dbName = coluna.dbName(); // Nome da coluna no banco
                        String javaSetName = coluna.setJavaName(); // Nome do método setter na classe Java
                        Class<?> classField = field.getType(); // Tipo do campo Java
                        try {
                            // Obtém o método setter correspondente e o invoca para definir o valor
                            Method method = entity.getClass().getMethod(javaSetName, classField);
                            setValueByType(entity, method, classField, rs, dbName); // Chama método auxiliar para setar o valor
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            LOGGER.error("Erro ao setar valor em objeto consultado para campo {}. Verifique setJavaName na anotação ColunaTabela. Detalhes: {}", field.getName(), e.getMessage(), e);
                            throw new DAOException("ERRO AO SETAR VALOR EM OBJETO CONSULTADO. Verifique setJavaName na anotação ColunaTabela. Detalhes: " + e.getMessage(), e);
                        } catch (TipoElementoNaoConhecidoException e) {
                            LOGGER.error("Erro ao setar valor: tipo de dado não reconhecido para campo {}. Detalhes: {}", field.getName(), e.getMessage(), e);
                            throw new DAOException("ERRO AO SETAR VALOR: TIPO DE DADO NÃO RECONHECIDO para campo " + field.getName() + ". Detalhes: " + e.getMessage(), e);
                        }
                    }
                }
                LOGGER.info("Entidade {} com chave {} consultada com sucesso.", entity.getClass().getSimpleName(), valor);
                return entity; // Retorna a entidade preenchida
            }
            LOGGER.info("Entidade com chave {} não encontrada.", valor);
            return null; // Retorna null se nenhum registro for encontrado

        } catch (SQLException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | TipoChaveNaoEncontradaException e) {
            LOGGER.error("Erro ao consultar objeto com chave {}: {}", valor, e.getMessage(), e);
            throw new DAOException("ERRO CONSULTANDO OBJETO: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(connection, stm, rs); // Fecha recursos
        }
    }

    /**
     * Obtém o nome da coluna no banco de dados que corresponde à chave lógica da entidade.
     * @param clazz A classe da entidade.
     * @return O nome da coluna chave no banco de dados.
     * @throws TipoChaveNaoEncontradaException Se a anotação @TipoChave ou @ColunaTabela não for encontrada no campo da chave.
     */
    public String getNomeCampoChave(Class<?> clazz) throws TipoChaveNaoEncontradaException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // Procura o campo que possui tanto @TipoChave quanto @ColunaTabela
            if (field.isAnnotationPresent(TipoChave.class) && field.isAnnotationPresent(ColunaTabela.class)) {
                ColunaTabela coluna = field.getAnnotation(ColunaTabela.class);
                return coluna.dbName(); // Retorna o nome da coluna no banco
            }
        }
        // Se a chave não for encontrada ou não estiver mapeada, lança exceção
        String msg = "Nome do campo chave no banco de dados não encontrado. Verifique @TipoChave e @ColunaTabela no domínio " + clazz.getSimpleName();
        LOGGER.error(msg);
        throw new TipoChaveNaoEncontradaException(msg);
    }

    /**
     * Define o valor de um campo de uma entidade usando o método setter apropriado,
     * convertendo o valor do ResultSet para o tipo correto.
     * @param entity A entidade cujo campo será definido.
     * @param method O método setter a ser invocado.
     * @param classField O tipo da classe do campo.
     * @param rs O ResultSet de onde o valor será lido.
     * @param fieldName O nome da coluna no ResultSet.
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     * @throws TipoElementoNaoConhecidoException Se o tipo do campo não for suportado.
     */
    private void setValueByType(T entity, Method method, Class<?> classField, ResultSet rs, String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, TipoElementoNaoConhecidoException {
        // Trata diferentes tipos de dados para garantir a conversão correta
        if (classField.equals(Integer.class)) {
            Integer val = rs.getInt(fieldName);
            method.invoke(entity, val);
        } else if (classField.equals(Long.class)) {
            Long val = rs.getLong(fieldName);
            method.invoke(entity, val);
        } else if (classField.equals(Double.class)) {
            Double val = rs.getDouble(fieldName);
            method.invoke(entity, val);
        } else if (classField.equals(Short.class)) {
            Short val = rs.getShort(fieldName);
            method.invoke(entity, val);
        } else if (classField.equals(BigDecimal.class)) {
            BigDecimal val = rs.getBigDecimal(fieldName);
            method.invoke(entity, val);
        } else if (classField.equals(String.class)) {
            String val = rs.getString(fieldName);
            method.invoke(entity, val);
        } else if (classField.equals(java.time.Instant.class)) { // Suporte a java.time.Instant
            java.sql.Timestamp timestamp = rs.getTimestamp(fieldName);
            method.invoke(entity, timestamp != null ? timestamp.toInstant() : null);
        } else if (classField.equals(br.com.rpires.domain.Venda.Status.class)) { // Suporte a Enum (Status da Venda)
            String statusStr = rs.getString(fieldName);
            method.invoke(entity, br.com.rpires.domain.Venda.Status.getByName(statusStr));
        } else {
            // Lança exceção se o tipo do campo não for reconhecido para o mapeamento
            String msg = "TIPO DE CLASSE NÃO CONHECIDO PARA SETTER: " + classField.getName();
            LOGGER.error(msg);
            throw new TipoElementoNaoConhecidoException(msg);
        }
    }

    /**
     * Verifica se há mais de um registro para uma dada chave.
     * Usado principalmente antes de consultas que esperam um único resultado.
     * @param valor A chave a ser verificada.
     * @return O número de registros encontrados.
     * @throws MaisDeUmRegistroException Se mais de um registro for encontrado.
     * @throws TableException Se houver um problema com o nome da tabela.
     * @throws TipoChaveNaoEncontradaException Se a chave não for encontrada ou configurada.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    protected Long validarMaisDeUmRegistro(E valor) throws MaisDeUmRegistroException, TableException, TipoChaveNaoEncontradaException, DAOException {
        Connection connection = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        Long count = null;
        try {
            connection = ConnectionFactory.getConnection();
            // Constrói a query COUNT para verificar a quantidade de registros
            String tableName = getTableName();
            String keyFieldName = getNomeCampoChave(getTipoClasse());
            String sql = "SELECT count(*) FROM " + tableName + " WHERE " + keyFieldName + " = ?";
            stm = connection.prepareStatement(sql);
            setParametrosQuerySelect(stm, valor); // Define o parâmetro da query
            rs = stm.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1); // Obtém a contagem
                if (count > 1) {
                    String msg = "ENCONTRADO MAIS DE UM REGISTRO DE " + tableName + " PARA A CHAVE: " + valor;
                    LOGGER.error(msg);
                    throw new MaisDeUmRegistroException(msg);
                }
            }
            return count;
        } catch (SQLException e) {
            LOGGER.error("Erro SQL ao validar mais de um registro para chave {}: {}", valor, e.getMessage(), e);
            throw new DAOException("ERRO AO VALIDAR MAIS DE UM REGISTRO. Detalhes: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(connection, stm, rs); // Fecha recursos
        }
    }

    /**
     * Obtém o nome da tabela no banco de dados para a entidade.
     * O nome é extraído da anotação @Tabela da classe da entidade.
     * @return O nome da tabela.
     * @throws TableException Se a anotação @Tabela não for encontrada na classe.
     */
    protected String getTableName() throws TableException {
        // Verifica se a classe da entidade possui a anotação @Tabela
        if (getTipoClasse().isAnnotationPresent(Tabela.class)) {
            Tabela table = getTipoClasse().getAnnotation(Tabela.class);
            return table.value(); // Retorna o valor da anotação (o nome da tabela)
        } else {
            // Lança exceção se a anotação @Tabela estiver faltando
            String msg = "TABELA NO TIPO " + getTipoClasse().getName() + " NÃO FOI ENCONTRADA. Verifique a anotação @Tabela.";
            LOGGER.error(msg);
            throw new TableException(msg);
        }
    }

    /**
     * Retorna todos os registros de uma determinada entidade/tabela no banco de dados.
     * @return Uma coleção de entidades encontradas. Pode ser vazia se não houver registros.
     * @throws DAOException Se ocorrer um erro de acesso a dados.
     */
    @Override
    public Collection<T> buscarTodos() throws DAOException {
        List<T> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            connection = ConnectionFactory.getConnection(); // Obtém a conexão
            String tableName = getTableName();
            String sql = "SELECT * FROM " + tableName;
            stm = connection.prepareStatement(sql); // Prepara a query para buscar todos
            rs = stm.executeQuery(); // Executa a consulta

            while (rs.next()) { // Itera sobre cada registro no ResultSet
                // Cria uma nova instância da entidade para cada registro
                T entity = getTipoClasse().getConstructor().newInstance();
                // Itera sobre os campos da entidade para preenchê-los
                for (Field field : entity.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(ColunaTabela.class)) {
                        ColunaTabela coluna = field.getAnnotation(ColunaTabela.class);
                        String dbName = coluna.dbName();
                        String javaSetName = coluna.setJavaName();
                        Class<?> classField = field.getType();
                        try {
                            // Obtém o método setter e define o valor
                            Method method = entity.getClass().getMethod(javaSetName, classField);
                            setValueByType(entity, method, classField, rs, dbName);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            LOGGER.error("Erro ao setar valor em objeto listado para campo {}. Verifique setJavaName na anotação ColunaTabela. Detalhes: {}", field.getName(), e.getMessage(), e);
                            throw new DAOException("ERRO AO SETAR VALOR EM OBJETO LISTADO. Verifique setJavaName na anotação ColunaTabela. Detalhes: " + e.getMessage(), e);
                        } catch (TipoElementoNaoConhecidoException e) {
                            LOGGER.error("Erro ao setar valor: tipo de dado não reconhecido para campo {}. Detalhes: {}", field.getName(), e.getMessage(), e);
                            throw new DAOException("ERRO AO SETAR VALOR: TIPO DE DADO NÃO RECONHECIDO para campo " + field.getName() + ". Detalhes: " + e.getMessage(), e);
                        }
                    }
                }
                list.add(entity); // Adiciona a entidade preenchida à lista
            }
            LOGGER.info("Total de {} entidades do tipo {} encontradas.", list.size(), getTipoClasse().getSimpleName());
            return list; // Retorna a lista de entidades

        } catch (SQLException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | TableException e) {
            LOGGER.error("Erro ao listar objetos do tipo {}: {}", getTipoClasse().getSimpleName(), e.getMessage(), e);
            throw new DAOException("ERRO LISTANDO OBJETOS. Detalhes: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(connection, stm, rs); // Fecha recursos
        }
    }
}