package br.com.rpires.domain;

import br.com.rpires.infra.annotations.ColunaTabela;
import br.com.rpires.infra.annotations.Tabela;
import br.com.rpires.infra.annotations.TipoChave;

/**
 * Classe que representa a entidade Cliente no sistema.
 * Mapeada para a tabela TB_CLIENTE no banco de dados.
 */
@Tabela("TB_CLIENTE") // Mapeia esta classe para a tabela "TB_CLIENTE"
public class Cliente implements Persistente {

    private Long id; // ID técnico, gerado pelo banco de dados (chave primária)

    @TipoChave("getCpf") // Indica que o método getCpf() retorna a chave lógica do objeto para consultas
    @ColunaTabela(dbName = "CPF", setJavaName = "setCpf") // Mapeia para a coluna CPF e usa setCpf()
    private Long cpf; // CPF do cliente, chave única lógica

    @ColunaTabela(dbName = "NOME", setJavaName = "setNome") // Mapeia para a coluna NOME e usa setNome()
    private String nome;

    @ColunaTabela(dbName = "TEL", setJavaName = "setTel") // Mapeia para a coluna TEL e usa setTel()
    private Long tel;

    @ColunaTabela(dbName = "ENDERECO", setJavaName = "setEnd") // Mapeia para a coluna ENDERECO e usa setEnd()
    private String end;

    @ColunaTabela(dbName = "NUMERO", setJavaName = "setNumero") // Mapeia para a coluna NUMERO e usa setNumero()
    private Integer numero;

    @ColunaTabela(dbName = "CIDADE", setJavaName = "setCidade") // Mapeia para a coluna CIDADE e usa setCidade()
    private String cidade;

    @ColunaTabela(dbName = "ESTADO", setJavaName = "setEstado") // Mapeia para a coluna ESTADO e usa setEstado()
    private String estado;

    // Construtores

    public Cliente() {
        // Construtor padrão, necessário para a reflexão em GenericDAO
    }

    /**
     * Construtor para criar um objeto Cliente.
     * @param cpf O CPF do cliente.
     * @param nome O nome do cliente.
     * @param tel O telefone do cliente.
     * @param end O endereço do cliente.
     * @param numero O número do endereço.
     * @param cidade A cidade do cliente.
     * @param estado O estado do cliente.
     */
    public Cliente(Long cpf, String nome, Long tel, String end, Integer numero, String cidade, String estado) {
        this.cpf = cpf;
        this.nome = nome;
        this.tel = tel;
        this.end = end;
        this.numero = numero;
        this.cidade = cidade;
        this.estado = estado;
    }

    // Getters e Setters (implementam Persistente e os métodos mapeados pelas anotações)

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getTel() {
        return tel;
    }

    public void setTel(Long tel) {
        this.tel = tel;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Cliente{" +
               "id=" + id +
               ", cpf=" + cpf +
               ", nome='" + nome + '\'' +
               ", tel=" + tel +
               ", end='" + end + '\'' +
               ", numero=" + numero +
               ", cidade='" + cidade + '\'' +
               ", estado='" + estado + '\'' +
               '}';
    }
}