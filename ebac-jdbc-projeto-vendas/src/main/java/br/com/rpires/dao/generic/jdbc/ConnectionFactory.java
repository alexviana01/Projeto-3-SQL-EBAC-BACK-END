package br.com.rpires.dao.generic.jdbc;

import br.com.rpires.exceptions.DAOException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados PostgreSQL
 * utilizando HikariCP para pooling de conexões.
 * Implementa o padrão Singleton para o HikariDataSource.
 */
public class ConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);
    private static HikariDataSource dataSource; // A instância única do HikariDataSource

    // Bloco estático para inicializar o HikariDataSource uma única vez
    static {
        LOGGER.info("Inicializando HikariCP Connection Pool...");
        HikariConfig config = new HikariConfig();
        // URL de conexão para PostgreSQL
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/vendas_online_2");
        config.setUsername("postgres");
        config.setPassword("2025al");

        // Configurações adicionais do pool (exemplo)
        config.setMaximumPoolSize(10); // Tamanho máximo do pool de conexões
        config.setMinimumIdle(5); // Número mínimo de conexões ociosas a serem mantidas
        config.setConnectionTimeout(30000); // Tempo máximo de espera por uma conexão (30 segundos)
        config.setIdleTimeout(600000); // Tempo máximo que uma conexão pode ficar ociosa no pool (10 minutos)
        config.setMaxLifetime(1800000); // Tempo máximo de vida de uma conexão (30 minutos)

        // Configurações para otimização de PreparedStatement (importante para JDBC direto)
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            dataSource = new HikariDataSource(config);
            LOGGER.info("HikariCP Connection Pool inicializado com sucesso.");
        } catch (Exception e) {
            LOGGER.error("Falha ao inicializar o HikariCP Connection Pool: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao inicializar o pool de conexões.", e);
        }
    }

    // Construtor privado para impedir instâncias diretas (padrão Singleton)
    private ConnectionFactory() {
        // O construtor não faz nada, a conexão é obtida via método estático
    }

    /**
     * Obtém uma instância de conexão com o banco de dados do pool.
     * Se o pool não estiver inicializado, o bloco estático cuidará disso.
     * @return Uma instância ativa de Connection.
     * @throws DAOException Se ocorrer um erro ao obter a conexão do pool.
     */
    public static Connection getConnection() throws DAOException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error("Erro ao obter conexão do pool: {}", e.getMessage(), e);
            throw new DAOException("ERRO AO OBTER CONEXAO COM O BANCO DE DADOS: " + e.getMessage(), e);
        }
    }

    /**
     * Fecha os recursos do banco de dados (ResultSet, PreparedStatement, Connection).
     * Quando uma Connection obtida do HikariCP é fechada, ela é retornada ao pool, não fisicamente fechada.
     * @param connection A conexão a ser fechada (retornada ao pool).
     * @param stm O PreparedStatement a ser fechado.
     * @param rs O ResultSet a ser fechado.
     */
    public static void closeConnection(Connection connection, PreparedStatement stm, ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (stm != null && !stm.isClosed()) {
                stm.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close(); // Retorna a conexão ao pool
            }
        } catch (SQLException e) {
            LOGGER.error("Erro ao fechar recursos do banco (ResultSet, PreparedStatement, Connection): {}", e.getMessage(), e);
        }
    }

    /**
     * Fecha explicitamente o pool de conexões do HikariCP.
     * Este método deve ser chamado apenas ao desligar a aplicação para liberar todos os recursos do pool.
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            LOGGER.info("Fechando HikariCP Connection Pool...");
            dataSource.close();
            LOGGER.info("HikariCP Connection Pool fechado.");
        }
    }
}