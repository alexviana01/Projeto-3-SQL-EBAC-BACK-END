package br.com.rpires.domain;

import br.com.rpires.infra.annotations.ColunaTabela;
import br.com.rpires.infra.annotations.Tabela;
import br.com.rpires.infra.annotations.TipoChave;

import java.math.BigDecimal;

/**
 * Classe que representa a entidade Produto no sistema.
 * Mapeada para a tabela TB_PRODUTO no banco de dados.
 */
@Tabela("TB_PRODUTO") // Mapeia esta classe para a tabela "TB_PRODUTO"
public class Produto implements Persistente {

    private Long id; // ID técnico, gerado pelo banco de dados (chave primária)

    @TipoChave("getCodigo") // Indica que o método getCodigo() retorna a chave lógica para consultas
    @ColunaTabela(dbName = "CODIGO", setJavaName = "setCodigo") // Mapeia para a coluna CODIGO e usa setCodigo()
    private String codigo; // Código do produto, chave única lógica

    @ColunaTabela(dbName = "NOME", setJavaName = "setNome") // Mapeia para a coluna NOME e usa setNome()
    private String nome;

    @ColunaTabela(dbName = "DESCRICAO", setJavaName = "setDescricao") // Mapeia para a coluna DESCRICAO e usa setDescricao()
    private String descricao;

    @ColunaTabela(dbName = "VALOR", setJavaName = "setValor") // Mapeia para a coluna VALOR e usa setValor()
    private BigDecimal valor; // Valor unitário do produto

    // Construtores

    public Produto() {
        // Construtor padrão, necessário para a reflexão em GenericDAO
    }

    /**
     * Construtor para criar um objeto Produto.
     * @param codigo O código do produto.
     * @param nome O nome do produto.
     * @param descricao A descrição do produto.
     * @param valor O valor unitário do produto.
     */
    public Produto(String codigo, String nome, String descricao, BigDecimal valor) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Produto{" +
               "id=" + id +
               ", codigo='" + codigo + '\'' +
               ", nome='" + nome + '\'' +
               ", descricao='" + descricao + '\'' +
               ", valor=" + valor +
               '}';
    }
}