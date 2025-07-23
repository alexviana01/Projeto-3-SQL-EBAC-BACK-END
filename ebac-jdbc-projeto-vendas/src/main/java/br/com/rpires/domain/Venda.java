package br.com.rpires.domain;

import br.com.rpires.infra.annotations.ColunaTabela;
import br.com.rpires.infra.annotations.Tabela;
import br.com.rpires.infra.annotations.TipoChave;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Classe que representa a entidade Venda no sistema.
 * Mapeada para a tabela TB_VENDA no banco de dados.
 */
@Tabela("TB_VENDA") // Mapeia esta classe para a tabela "TB_VENDA"
public class Venda implements Persistente {

    /**
     * Enumeração para o status de uma venda.
     */
    public enum Status {
        INICIADA("Iniciada"), // Venda recém-criada
        CONCLUIDA("Concluída"), // Venda finalizada e paga
        CANCELADA("Cancelada"); // Venda cancelada

        private String nome;

        Status(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }

        /**
         * Retorna o Status correspondente ao nome (case-insensitive).
         * Útil para converter strings do banco de dados para o enum.
         * @param name O nome do status.
         * @return O objeto Status correspondente, ou null se não encontrado.
         */
        public static Status getByName(String name) {
            for (Status status : Status.values()) {
                if (status.name().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            // Lançar exceção pode ser melhor em produção para nomes inválidos
            throw new IllegalArgumentException("Status de venda desconhecido: " + name);
        }
    }

    private Long id; // ID técnico, gerado pelo banco de dados (chave primária)

    @TipoChave("getCodigo") // Indica que o método getCodigo() retorna a chave lógica para consultas
    @ColunaTabela(dbName = "CODIGO", setJavaName = "setCodigo") // Mapeia para a coluna CODIGO e usa setCodigo()
    private String codigo; // Código da venda, chave única lógica

    private Cliente cliente; // O cliente associado a esta venda (relação 1:N)

    @ColunaTabela(dbName = "VALOR_TOTAL", setJavaName = "setValorTotal") // Mapeia para a coluna VALOR_TOTAL e usa setValorTotal()
    private BigDecimal valorTotal; // Valor total da venda

    @ColunaTabela(dbName = "DATA_VENDA", setJavaName = "setDataVenda") // Mapeia para a coluna DATA_VENDA e usa setDataVenda()
    private Instant dataVenda; // Data e hora da venda

    @ColunaTabela(dbName = "STATUS_VENDA", setJavaName = "setStatus") // Mapeia para a coluna STATUS_VENDA e usa setStatus()
    private Status status; // Status atual da venda (enum)

    private Set<ProdutoQuantidade> produtos; // Conjunto de itens (ProdutoQuantidade) da venda (relação N:M)

    // Construtor

    public Venda() {
        this.produtos = new HashSet<>(); // Inicializa o conjunto de produtos
        this.valorTotal = BigDecimal.ZERO; // Inicializa o valor total como zero
    }

    // Getters e Setters

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Instant getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(Instant dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<ProdutoQuantidade> getProdutos() {
        return produtos;
    }

    /**
     * Define o conjunto de produtos da venda.
     * Recalcula o valor total da venda após definir os produtos.
     * @param produtos O conjunto de ProdutoQuantidade a ser associado à venda.
     */
    public void setProdutos(Set<ProdutoQuantidade> produtos) {
        this.produtos = Objects.requireNonNullElseGet(produtos, HashSet::new); // Garante que nunca seja nulo
        recalcularValorTotalVenda(); // Garante que o valor total seja recalculado ao definir os produtos
    }

    /**
     * Adiciona um produto à lista de produtos da venda. Se o produto já existir,
     * a quantidade é atualizada. Recalcula o valor total da venda.
     * @param produto O produto a ser adicionado.
     * @param quantidade A quantidade do produto.
     */
    public void adicionarProduto(Produto produto, Integer quantidade) {
        Objects.requireNonNull(produto, "Produto não pode ser nulo.");
        Objects.requireNonNull(quantidade, "Quantidade não pode ser nula.");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        // Busca se o produto já existe na lista de itens da venda
        ProdutoQuantidade pq = this.produtos.stream()
                                            .filter(p -> p.getProduto().getCodigo().equals(produto.getCodigo()))
                                            .findFirst()
                                            .orElse(null);
        if (pq != null) {
            // Se existir, adiciona a quantidade
            pq.adicionar(quantidade);
        } else {
            // Se não existir, cria um novo item ProdutoQuantidade
            ProdutoQuantidade novoPQ = new ProdutoQuantidade(produto, quantidade);
            this.produtos.add(novoPQ);
        }
        recalcularValorTotalVenda(); // Sempre recalcula o valor total da venda
    }

    /**
     * Remove uma quantidade de um produto da venda. Se a quantidade se tornar zero ou menos,
     * o item é removido completamente da venda. Recalcula o valor total da venda.
     * @param produto O produto a ser removido.
     * @param quantidade A quantidade a ser removida.
     */
    public void removerProduto(Produto produto, Integer quantidade) {
        Objects.requireNonNull(produto, "Produto não pode ser nulo.");
        Objects.requireNonNull(quantidade, "Quantidade não pode ser nula.");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero para remoção.");
        }

        // Busca se o produto já existe na lista de itens da venda
        ProdutoQuantidade pq = this.produtos.stream()
                                            .filter(p -> p.getProduto().getCodigo().equals(produto.getCodigo()))
                                            .findFirst()
                                            .orElse(null);
        if (pq != null) {
            // Se existir, remove a quantidade
            pq.remover(quantidade);
            if (pq.getQuantidade() <= 0) {
                // Se a quantidade for zero ou negativa, remove o item da venda
                this.produtos.remove(pq);
            }
        } else {
            // Opcional: Lançar exceção se tentar remover produto que não existe
            // throw new IllegalArgumentException("Produto não encontrado na venda para remoção.");
        }
        recalcularValorTotalVenda(); // Sempre recalcula o valor total da venda
    }

    /**
     * Recalcula o valor total da venda somando os valores totais de todos os seus itens (ProdutoQuantidade).
     */
    public void recalcularValorTotalVenda() {
        this.valorTotal = this.produtos.stream()
                                       .map(ProdutoQuantidade::getValorTotal)
                                       .filter(Objects::nonNull) // Garante que não haverá NPE se um valor for nulo
                                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return "Venda{" +
               "id=" + id +
               ", codigo='" + codigo + '\'' +
               ", cliente=" + (cliente != null ? cliente.getNome() + " (ID: " + cliente.getId() + ")" : "N/A") +
               ", valorTotal=" + valorTotal +
               ", dataVenda=" + dataVenda +
               ", status=" + status +
               ", produtos=" + (produtos != null ? produtos.size() : 0) + " items" +
               '}';
    }
}