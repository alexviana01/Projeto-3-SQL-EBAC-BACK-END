package br.com.rpires.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Classe que representa um item dentro de uma Venda, registrando
 * qual Produto foi vendido, em qual Quantidade e qual o Valor Total para esse item.
 */
public class ProdutoQuantidade implements Persistente {

    private Long id; // ID técnico do item na tabela N:M (TB_PRODUTO_QUANTIDADE)
    private Produto produto; // O produto associado a este item
    private Integer quantidade; // A quantidade do produto neste item
    private BigDecimal valorTotal; // O valor total para este item (quantidade * valor unitário do produto)

    public ProdutoQuantidade() {
        this.quantidade = 0;
        this.valorTotal = BigDecimal.ZERO;
    }

    /**
     * Construtor para criar um objeto ProdutoQuantidade com produto e quantidade iniciais.
     * @param produto O produto associado.
     * @param quantidade A quantidade do produto.
     */
    public ProdutoQuantidade(Produto produto, Integer quantidade) {
        this.produto = produto;
        this.quantidade = Objects.requireNonNullElse(quantidade, 0); // Garante que a quantidade não seja nula
        calcularValorTotal();
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

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        calcularValorTotal(); // Recalcula o valor total do item ao definir o produto
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = Objects.requireNonNullElse(quantidade, 0);
        calcularValorTotal(); // Recalcula o valor total do item ao definir a quantidade
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    /**
     * Adiciona uma quantidade ao item e recalcula o valor total.
     * @param qtd Quantidade a ser adicionada.
     */
    public void adicionar(Integer qtd) {
        this.quantidade += Objects.requireNonNullElse(qtd, 0);
        calcularValorTotal();
    }

    /**
     * Remove uma quantidade do item e recalcula o valor total.
     * @param qtd Quantidade a ser removida.
     */
    public void remover(Integer qtd) {
        this.quantidade -= Objects.requireNonNullElse(qtd, 0);
        calcularValorTotal();
    }

    /**
     * Calcula o valor total do item (quantidade * valor do produto).
     * Garante que o valor total esteja sempre atualizado.
     */
    private void calcularValorTotal() {
        if (this.produto != null && this.produto.getValor() != null && this.quantidade != null) {
            this.valorTotal = this.produto.getValor().multiply(new BigDecimal(this.quantidade));
        } else {
            this.valorTotal = BigDecimal.ZERO; // Caso o produto ou quantidade não estejam definidos
        }
    }

    @Override
    public String toString() {
        return "ProdutoQuantidade{" +
               "id=" + id +
               ", produto=" + (produto != null ? produto.getNome() + " (" + produto.getCodigo() + ")" : "N/A") +
               ", quantidade=" + quantidade +
               ", valorTotal=" + valorTotal +
               '}';
    }
}