package br.com.rpires.infra.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de classe que define o mapeamento entre uma classe de domínio
 * e o nome da tabela correspondente no banco de dados.
 */
@Documented // Indica que a anotação deve ser incluída na documentação gerada
@Target(ElementType.TYPE) // Define que esta anotação pode ser usada apenas em tipos (classes, interfaces, enums)
@Retention(RetentionPolicy.RUNTIME) // A anotação estará disponível em tempo de execução via reflexão
public @interface Tabela {

    /**
     * O nome da tabela no banco de dados para a qual a classe está mapeada.
     * Ex: "TB_CLIENTE", "TB_PRODUTO"
     * @return O nome da tabela.
     */
    String value();
}