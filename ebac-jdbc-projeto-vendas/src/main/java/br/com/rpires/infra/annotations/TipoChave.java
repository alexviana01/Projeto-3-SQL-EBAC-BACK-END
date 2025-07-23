package br.com.rpires.infra.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de campo que indica qual atributo de uma classe de domínio
 * representa a "chave lógica" ou identificador principal que será usado
 * em operações de consulta e exclusão.
 * <p>
 * O valor da anotação deve ser o nome do método 'getter' para esse atributo.
 */
@Documented // Indica que a anotação deve ser incluída na documentação gerada
@Target(ElementType.FIELD) // Define que esta anotação pode ser usada apenas em campos
@Retention(RetentionPolicy.RUNTIME) // A anotação estará disponível em tempo de execução via reflexão
public @interface TipoChave {

    /**
     * O nome do método 'getter' do atributo que serve como chave lógica.
     * Ex: "getCpf" para a classe Cliente, "getCodigo" para a classe Produto.
     * @return O nome do método getter da chave.
     */
    String value();
}