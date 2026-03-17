package br.com.fiap.restaurant.pedido.core.presenter;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.domain.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para o presenter OrderItemPresenter")
class OrderItemPresenterTest {

    @Nested
    @DisplayName("Conversão para Output")
    class ToOutput {

        @Test
        @DisplayName("Deve converter um OrderItem para OrderItemOutput com sucesso")
        void deveConverterOrderItemParaOutputComSucesso() {
            // Given
            var menuItem = new MenuItem(1L, "Cheeseburger", new BigDecimal("25.50"), false, 1L);
            var orderItem = new OrderItem(menuItem, new BigDecimal("2"));
            var expectedTotal = new BigDecimal("51.00");

            // When
            var orderItemOutput = OrderItemPresenter.toOutput(orderItem);

            // Then
            assertThat(orderItemOutput).isNotNull();
            assertThat(orderItemOutput.menuItemId()).isEqualTo(menuItem.getId());
            assertThat(orderItemOutput.name()).isEqualTo(menuItem.getName());
            assertThat(orderItemOutput.quantity()).isEqualTo(orderItem.getQuantity());
            assertThat(orderItemOutput.unitPrice()).isEqualTo(menuItem.getUnitPrice());
            assertThat(orderItemOutput.total()).isEqualTo(expectedTotal);
        }
    }
}
