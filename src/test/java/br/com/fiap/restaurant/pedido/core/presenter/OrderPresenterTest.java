package br.com.fiap.restaurant.pedido.core.presenter;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.OrderItem;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para o presenter OrderPresenter")
class OrderPresenterTest {

    @Nested
    @DisplayName("Conversão para Output")
    class ToOutput {

        @Test
        @DisplayName("Deve converter um Order para OrderOutput com sucesso")
        void deveConverterOrderParaOutputComSucesso() {
            // Given
            var menuItem1 = new MenuItem(1L, "Cheeseburger", new BigDecimal("25.50"), false, 1L);
            var orderItem1 = new OrderItem(menuItem1, new BigDecimal("2")); // Total: 51.00

            var menuItem2 = new MenuItem(2L, "Fries", new BigDecimal("10.00"), false, 1L);
            var orderItem2 = new OrderItem(menuItem2, new BigDecimal("1")); // Total: 10.00

            var order = new Order(
                    100L,
                    1L,
                    UUID.randomUUID(),
                    List.of(orderItem1, orderItem2),
                    LocalDateTime.now(),
                    StatusOrder.DRAFT
            );
            var expectedTotal = new BigDecimal("61.00");

            // When
            var orderOutput = OrderPresenter.toOutput(order);

            // Then
            assertThat(orderOutput).isNotNull();
            assertThat(orderOutput.id()).isEqualTo(order.getId());
            assertThat(orderOutput.restaurantId()).isEqualTo(order.getRestaurantId());
            assertThat(orderOutput.customerUuid()).isEqualTo(order.getCustomerUuid());
            assertThat(orderOutput.status()).isEqualTo(order.getStatus());
            assertThat(orderOutput.orderDateTime()).isEqualTo(order.getOrderDateTime());
            assertThat(orderOutput.total()).isEqualTo(expectedTotal);

            assertThat(orderOutput.items()).hasSize(2);
            assertThat(orderOutput.items().get(0).menuItemId()).isEqualTo(menuItem1.getId());
            assertThat(orderOutput.items().get(1).menuItemId()).isEqualTo(menuItem2.getId());
        }
    }
}
