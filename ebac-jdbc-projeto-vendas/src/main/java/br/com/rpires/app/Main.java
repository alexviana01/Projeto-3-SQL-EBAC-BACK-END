package br.com.rpires.app;

import br.com.rpires.dao.ClienteDAO;

import br.com.rpires.dao.ProdutoDAO;
import br.com.rpires.dao.VendaDAO;
import br.com.rpires.domain.Cliente;
import br.com.rpires.domain.Produto;
import br.com.rpires.domain.Venda;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.MaisDeUmRegistroException;
import br.com.rpires.exceptions.TableException;
import br.com.rpires.exceptions.TipoChaveNaoEncontradaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;

/**
 * Classe principal para executar e demonstrar as funcionalidades do projeto EBAC Professor JDBC.
 * Atua como um ponto de entrada para testar as operações CRUD (Create, Read, Update, Delete)
 * de Cliente, Produto e Venda, incluindo cenários com múltiplos itens e estados de venda.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("======================================================");
        LOGGER.info("===== Iniciando a execução do projeto EBAC JDBC =====");
        LOGGER.info("======================================================");

        // Instanciando os Data Access Objects (DAOs) para interagir com o banco de dados.
        // Estes DAOs encapsulam a lógica de persistência e recuperação de dados,
        // utilizando o GenericDAO e a ConnectionFactory.
        ClienteDAO clienteDAO = new ClienteDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();
        VendaDAO vendaDAO = new VendaDAO();

        try {
            // Recomenda-se TRUNCAR as tabelas no pgAdmin ANTES de cada execução
            // para garantir um teste limpo, já que VendaDAO não suporta exclusão via método genérico
            // e os IDs de sequência são incrementais.
            // SQL para TRUNCATE (executar no pgAdmin antes de cada run para limpar dados de teste):
            // TRUNCATE TABLE tb_produto_quantidade, tb_venda, tb_produto, tb_cliente RESTART IDENTITY;

            // -----------------------------------------------------------
            // --- CENÁRIO 1: Demonstração das operações do ClienteDAO ---
            // -----------------------------------------------------------
            LOGGER.info("\n--- Testando ClienteDAO: Cadastro, Consulta, Atualização e Exclusão ---");

            // 1. Criação e Cadastro de um novo Cliente
            LOGGER.info("\n>>> Operação: Cadastrar Cliente <<<");
            Cliente cliente1 = new Cliente();
            cliente1.setCpf(11122233344L); // CPF como chave única, importante para consultas e exclusões
            cliente1.setNome("Mariana Souza");
            cliente1.setTel(21987654321L);
            cliente1.setEnd("Av. Atlântica, 123");
            cliente1.setNumero(123);
            cliente1.setCidade("Rio de Janeiro");
            cliente1.setEstado("RJ");

            LOGGER.info("Tentando cadastrar cliente: {} (CPF: {})", cliente1.getNome(), cliente1.getCpf());
            Boolean cadastradoCliente = clienteDAO.cadastrar(cliente1);
            if (cadastradoCliente) {
                LOGGER.info("SUCESSO: Cliente cadastrado! ID gerado: {}", cliente1.getId());
            } else {
                LOGGER.error("FALHA: Não foi possível cadastrar o cliente.");
            }

            // 2. Consulta de Cliente
            LOGGER.info("\n>>> Operação: Consultar Cliente <<<");
            Cliente clienteConsultado = clienteDAO.consultar(cliente1.getCpf());
            if (clienteConsultado != null) {
                LOGGER.info("SUCESSO: Cliente consultado. Dados: {}", clienteConsultado);
            } else {
                LOGGER.error("FALHA: Cliente com CPF {} não encontrado.", cliente1.getCpf());
            }

            // 3. Atualização de Cliente
            LOGGER.info("\n>>> Operação: Atualizar Cliente <<<");
            if (clienteConsultado != null) {
                String novoNome = "Mariana Souza Antunes";
                clienteConsultado.setNome(novoNome);
                clienteConsultado.setTel(21998877665L); // Novo telefone
                LOGGER.info("Atualizando nome e telefone do cliente para: {} / {}", novoNome, clienteConsultado.getTel());
                clienteDAO.alterar(clienteConsultado); // Executa a alteração no banco
                // Re-consulta para verificar a atualização
                Cliente clienteAtualizado = clienteDAO.consultar(clienteConsultado.getCpf());
                LOGGER.info("SUCESSO: Cliente atualizado. Novo nome: {}, Novo Tel: {}", clienteAtualizado.getNome(), clienteAtualizado.getTel());
            } else {
                LOGGER.warn("AVISO: Não foi possível atualizar o cliente, pois não foi encontrado para consulta inicial.");
            }

            // 4. Exclusão de Cliente
            LOGGER.info("\n>>> Operação: Excluir Cliente <<<");
            LOGGER.info("Tentando excluir cliente com CPF: {}", cliente1.getCpf());
            clienteDAO.excluir(cliente1.getCpf()); // Executa a exclusão
            // Tenta consultar novamente para confirmar a exclusão
            Cliente clienteExcluido = clienteDAO.consultar(cliente1.getCpf());
            if (clienteExcluido == null) {
                LOGGER.info("SUCESSO: Cliente com CPF {} excluído com sucesso.", cliente1.getCpf());
            } else {
                LOGGER.error("FALHA: Cliente com CPF {} ainda existe após a exclusão.", cliente1.getCpf());
            }

            // -----------------------------------------------------------
            // --- CENÁRIO 2: Demonstração das operações do ProdutoDAO ---
            // -----------------------------------------------------------
            LOGGER.info("\n--- Testando ProdutoDAO: Cadastro, Consulta e Atualização ---");

            // 1. Criação e Cadastro de um novo Produto
            LOGGER.info("\n>>> Operação: Cadastrar Produto <<<");
            Produto produto1 = new Produto();
            produto1.setCodigo("PROD_ABC");
            produto1.setNome("Smart TV 55 polegadas");
            produto1.setDescricao("TV LED 4K, Smart, com controle por voz.");
            produto1.setValor(new BigDecimal("2999.99"));

            LOGGER.info("Tentando cadastrar produto: {} (Código: {})", produto1.getNome(), produto1.getCodigo());
            Boolean cadastradoProduto = produtoDAO.cadastrar(produto1);
            if (cadastradoProduto) {
                LOGGER.info("SUCESSO: Produto cadastrado! ID gerado: {}", produto1.getId());
            } else {
                LOGGER.error("FALHA: Não foi possível cadastrar o produto.");
            }

            // 2. Consulta de Produto
            LOGGER.info("\n>>> Operação: Consultar Produto <<<");
            Produto produtoConsultado = produtoDAO.consultar(produto1.getCodigo());
            if (produtoConsultado != null) {
                LOGGER.info("SUCESSO: Produto consultado. Dados: {}", produtoConsultado);
            } else {
                LOGGER.error("FALHA: Produto com código {} não encontrado.", produto1.getCodigo());
            }

            // 3. Atualização de Produto
            LOGGER.info("\n>>> Operação: Atualizar Produto <<<");
            if (produtoConsultado != null) {
                BigDecimal novoValor = new BigDecimal("2750.00");
                produtoConsultado.setValor(novoValor);
                LOGGER.info("Atualizando valor do produto para: {}", novoValor);
                produtoDAO.alterar(produtoConsultado); // Executa a alteração
                // Re-consulta para verificar a atualização
                Produto produtoAtualizado = produtoDAO.consultar(produtoConsultado.getCodigo());
                LOGGER.info("SUCESSO: Produto atualizado. Novo valor: {}", produtoAtualizado.getValor());
            } else {
                LOGGER.warn("AVISO: Não foi possível atualizar o produto, pois não foi encontrado para consulta inicial.");
            }

            // 4. Exclusão de Produto
            LOGGER.info("\n>>> Operação: Excluir Produto <<<");
            LOGGER.info("Tentando excluir produto com código: {}", produto1.getCodigo());
            produtoDAO.excluir(produto1.getCodigo()); // Executa a exclusão
            // Tenta consultar novamente para confirmar a exclusão
            Produto produtoExcluido = produtoDAO.consultar(produto1.getCodigo());
            if (produtoExcluido == null) {
                LOGGER.info("SUCESSO: Produto com código {} excluído com sucesso.", produto1.getCodigo());
            } else {
                LOGGER.error("FALHA: Produto com código {} ainda existe após a exclusão.", produto1.getCodigo());
            }

            // -----------------------------------------------------------
            // --- CENÁRIO 3: Demonstração das operações do VendaDAO ---
            // -----------------------------------------------------------
            LOGGER.info("\n--- Testando VendaDAO: Cadastro, Consulta e Finalização ---");

            // Para testar vendas, precisamos de um cliente e produtos ativos no banco.
            // Recadastrando cliente e produtos temporários.
            LOGGER.info("\n>>> Preparação: Recadastrando Cliente e Produtos para Venda <<<");

            // Recria o cliente
            Cliente clienteVenda = new Cliente();
            clienteVenda.setCpf(55566677788L); // Novo CPF para evitar conflitos com exclusão anterior
            clienteVenda.setNome("Carlos Pereira");
            clienteVenda.setTel(11912345678L);
            clienteVenda.setEnd("Rua da Consolação, 500");
            clienteVenda.setNumero(500);
            clienteVenda.setCidade("São Paulo");
            clienteVenda.setEstado("SP");
            clienteDAO.cadastrar(clienteVenda);
            LOGGER.info("Cliente 'Carlos Pereira' recadastrado com ID: {}", clienteVenda.getId());

            // Recria Produto 1
            Produto produtoVenda1 = new Produto();
            produtoVenda1.setCodigo("PROD_XYZ");
            produtoVenda1.setNome("Smartphone X Pro");
            produtoVenda1.setDescricao("Smartphone de última geração com câmera avançada.");
            produtoVenda1.setValor(new BigDecimal("5000.00"));
            produtoDAO.cadastrar(produtoVenda1);
            LOGGER.info("Produto 'Smartphone X Pro' recadastrado com ID: {}", produtoVenda1.getId());

            // Recria Produto 2
            Produto produtoVenda2 = new Produto();
            produtoVenda2.setCodigo("PROD_QWE");
            produtoVenda2.setNome("Fone de Ouvido Bluetooth");
            produtoVenda2.setDescricao("Fone com cancelamento de ruído.");
            produtoVenda2.setValor(new BigDecimal("750.00"));
            produtoDAO.cadastrar(produtoVenda2);
            LOGGER.info("Produto 'Fone de Ouvido Bluetooth' recadastrado com ID: {}", produtoVenda2.getId());

            // 1. Criação e Cadastro de uma nova Venda
            LOGGER.info("\n>>> Operação: Cadastrar Venda <<<");
            Venda venda = new Venda();
            venda.setCodigo("VENDA_2024_001");
            venda.setCliente(clienteVenda); // Associar a venda ao cliente recadastrado
            venda.setDataVenda(Instant.now()); // Data/hora atual da venda
            venda.setStatus(Venda.Status.INICIADA); // Status inicial da venda

            // Adicionar produtos à venda usando o método adicionarProduto() da Venda
            venda.adicionarProduto(produtoVenda1, 1); // 1 Smartphone X Pro
            venda.adicionarProduto(produtoVenda2, 2); // 2 Fones de Ouvido

            // O valor total já é recalculado automaticamente ao adicionar produtos na entidade Venda
            LOGGER.info("Tentando cadastrar venda com código: {}, Cliente: {}, Valor Total Esperado: {}, Produtos: {} itens",
                    venda.getCodigo(),
                    clienteVenda.getNome(),
                    venda.getValorTotal(),
                    venda.getProdutos().size());
            Boolean cadastradoVenda = vendaDAO.cadastrar(venda);
            if (cadastradoVenda) {
                LOGGER.info("SUCESSO: Venda cadastrada! ID gerado: {}", venda.getId());
            } else {
                LOGGER.error("FALHA: Não foi possível cadastrar a venda.");
            }

            // 2. Consulta de Venda
            LOGGER.info("\n>>> Operação: Consultar Venda <<<");
            Venda vendaConsultada = vendaDAO.consultar(venda.getCodigo());
            if (vendaConsultada != null) {
                LOGGER.info("SUCESSO: Venda consultada. Detalhes:");
                LOGGER.info("  ID: {}", vendaConsultada.getId());
                LOGGER.info("  Código: {}", vendaConsultada.getCodigo());
                LOGGER.info("  Cliente: {} (CPF: {})", vendaConsultada.getCliente().getNome(), vendaConsultada.getCliente().getCpf());
                LOGGER.info("  Valor Total: {}", vendaConsultada.getValorTotal());
                LOGGER.info("  Status: {}", vendaConsultada.getStatus().getNome());
                LOGGER.info("  Produtos na Venda ({} itens):", vendaConsultada.getProdutos().size());
                vendaConsultada.getProdutos().forEach(pq ->
                        LOGGER.info("    - {} (código: {}) - Qtd: {}, Valor Item: {}",
                                pq.getProduto().getNome(), pq.getProduto().getCodigo(), pq.getQuantidade(), pq.getValorTotal()));
            } else {
                LOGGER.error("FALHA: Venda com código {} não encontrada.", venda.getCodigo());
            }

            // 3. Finalização de Venda
            LOGGER.info("\n>>> Operação: Finalizar Venda <<<");
            if (vendaConsultada != null && vendaConsultada.getStatus() == Venda.Status.INICIADA) {
                LOGGER.info("Finalizando venda com código: {}", vendaConsultada.getCodigo());
                vendaDAO.finalizarVenda(vendaConsultada); // Executa a finalização
                // Re-consultar para verificar o status atualizado
                Venda vendaFinalizada = vendaDAO.consultar(venda.getCodigo());
                LOGGER.info("SUCESSO: Venda {} agora está com status: {}", vendaFinalizada.getCodigo(), vendaFinalizada.getStatus().getNome());
            } else {
                LOGGER.warn("AVISO: Não foi possível finalizar a venda. Ela pode não ter sido encontrada ou já estar finalizada/cancelada.");
            }

            // 4. Listagem de Todas as Vendas
            LOGGER.info("\n--- Operação: Listar Todas as Vendas Cadastradas ---");
            Collection<Venda> todasVendas = vendaDAO.buscarTodos();
            if (todasVendas.isEmpty()) {
                LOGGER.info("Nenhuma venda encontrada no banco de dados.");
            } else {
                LOGGER.info("Vendas encontradas ({} no total):", todasVendas.size());
                todasVendas.forEach(v -> {
                    LOGGER.info("  Venda [Código: {}, Cliente: {}, Valor Total: {}, Status: {}, # Produtos: {}]",
                            v.getCodigo(),
                            (v.getCliente() != null ? v.getCliente().getNome() : "N/A"),
                            v.getValorTotal(),
                            v.getStatus().getNome(),
                            (v.getProdutos() != null ? v.getProdutos().size() : 0));
                });
            }

        } catch (TipoChaveNaoEncontradaException e) {
            LOGGER.error("ERRO (TipoChaveNaoEncontradaException): A chave primária do objeto não foi encontrada ou configurada incorretamente. Detalhes: {}", e.getMessage(), e);
            LOGGER.error("Stack Trace:", e);
        } catch (DAOException e) {
            LOGGER.error("ERRO (DAOException): Problema na camada de acesso a dados. Detalhes: {}", e.getMessage(), e);
            LOGGER.error("Stack Trace:", e);
        } catch (MaisDeUmRegistroException e) {
            LOGGER.error("ERRO (MaisDeUmRegistroException): Mais de um registro foi encontrado quando apenas um era esperado. Detalhes: {}", e.getMessage(), e);
            LOGGER.error("Stack Trace:", e);
        } catch (TableException e) {
            LOGGER.error("ERRO (TableException): Problema relacionado à configuração da tabela ou anotação @Tabela. Detalhes: {}", e.getMessage(), e);
            LOGGER.error("Stack Trace:", e);
        } catch (UnsupportedOperationException e) {
            LOGGER.error("ERRO (UnsupportedOperationException): Uma operação não suportada foi tentada. Detalhes: {}", e.getMessage(), e);
            LOGGER.error("Stack Trace:", e);
        } catch (Exception e) {
            LOGGER.error("ERRO INESPERADO: Ocorreu uma exceção não tratada. Detalhes: {}", e.getMessage(), e);
            LOGGER.error("Stack Trace:", e);
        } finally {
            LOGGER.info("\n======================================================");
            LOGGER.info("====== Execução do projeto EBAC JDBC finalizada. =====");
            LOGGER.info("======================================================");
            // Ao usar HikariCP, o fechamento da conexão (connection.close()) apenas retorna a conexão para o pool.
            // O pool em si é fechado automaticamente ao desligar a JVM, ou pode ser fechado explicitamente se necessário.
            // Para garantir que o pool seja fechado em uma aplicação de curta duração como esta, podemos chamar:
            // br.com.rpires.dao.generic.jdbc.ConnectionFactory.closeDataSource();
            // No entanto, para fins de demonstração, deixaremos que a JVM lide com isso ao sair.
        }
    }
}