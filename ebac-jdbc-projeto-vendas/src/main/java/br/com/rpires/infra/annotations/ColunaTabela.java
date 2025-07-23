package br.com.rpires.infra.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de campo que define o mapeamento entre um atributo de uma classe de domínio
 * e uma coluna em uma tabela de banco de dados.
 */
@Documented // Indica que a anotação deve ser incluída na documentação gerada (Javadoc)
@Target(ElementType.FIELD) // Define que esta anotação pode ser usada apenas em campos (atributos)
@Retention(RetentionPolicy.RUNTIME) // A anotação estará disponível em tempo de execução via reflexão
public @interface ColunaTabela {

    /**
     * O nome da coluna correspondente no banco de dados.
     * Ex: "NOME", "CPF", "ENDERECO"
     * @return O nome da coluna no banco de dados.
     */
    String dbName();

    /**
     * O nome do método 'setter' na classe de domínio que será usado para
     * definir o valor lido da coluna do banco de dados.
     * Ex: "setNome", "setCpf", "setEnd"
     * @return O nome do método setter.
     */
    String setJavaName();
}