package br.com.fiap.restaurant.pedido.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para o enum StatusOrder")
class StatusOrderTest {

    @Test
    @DisplayName("Deve conter os valores corretos do enum")
    void deveConterOsValoresCorretosDoEnum() {
        // Act & Assert
        assertThat(StatusOrder.values())
                .containsExactlyInAnyOrder(
                        StatusOrder.CREATED,
                        StatusOrder.APPROVED,
                        StatusOrder.PENDING_PAY,
                        StatusOrder.PAYED
                );
    }

    @Test
    @DisplayName("Deve retornar o nome correto para cada valor do enum")
    void deveRetornarNomeCorretoParaCadaValor() {
        // Act & Assert
        assertThat(StatusOrder.CREATED.name()).isEqualTo("CREATED");
        assertThat(StatusOrder.APPROVED.name()).isEqualTo("APPROVED");
        assertThat(StatusOrder.PENDING_PAY.name()).isEqualTo("PENDING_PAY");
        assertThat(StatusOrder.PAYED.name()).isEqualTo("PAYED");
    }

    @Test
    @DisplayName("Deve ser possível obter o valor do enum a partir de uma string")
    void deveObterValorDoEnumAPartirDeString() {
        // Act & Assert
        assertThat(StatusOrder.valueOf("CREATED")).isEqualTo(StatusOrder.CREATED);
        assertThat(StatusOrder.valueOf("APPROVED")).isEqualTo(StatusOrder.APPROVED);
        assertThat(StatusOrder.valueOf("PENDING_PAY")).isEqualTo(StatusOrder.PENDING_PAY);
        assertThat(StatusOrder.valueOf("PAYED")).isEqualTo(StatusOrder.PAYED);
    }
}
