package br.com.fiap.restaurant.pedido.core.domain;

import br.com.fiap.restaurant.pedido.core.exception.OperationNotAllowedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para a classe Order")
class OrderTest {

    private Long restaurantId;
    private UUID customerUuid;
    private List<OrderItem> items;
    private LocalDateTime orderDateTime;

    @BeforeEach
    void setUp() {
        restaurantId = 1L;
        customerUuid = UUID.randomUUID();
        orderDateTime = LocalDateTime.now();

        MenuItem menuItem1 = new MenuItem(1L, "Item A", new BigDecimal("10.00"), false, restaurantId);
        MenuItem menuItem2 = new MenuItem(2L, "Item B", new BigDecimal("20.00"), false, restaurantId);
        items = new ArrayList<>(List.of(
                new OrderItem(menuItem1, new BigDecimal("1")),
                new OrderItem(menuItem2, new BigDecimal("2"))
        ));
    }

    @Nested
    @DisplayName("Construtor")
    class Constructor {

        @Test
        @DisplayName("Deve criar um pedido com sucesso")
        void deveCriarPedidoComSucesso() {
            // Arrange
            var id = 1L;
            var status = StatusOrder.CREATED;

            // Act
            var order = new Order(id, restaurantId, customerUuid, items, orderDateTime, status);

            // Assert
            assertThat(order.getId()).isEqualTo(id);
            assertThat(order.getRestaurantId()).isEqualTo(restaurantId);
            assertThat(order.getCustomerUuid()).isEqualTo(customerUuid);
            assertThat(order.getItems()).isEqualTo(items);
            assertThat(order.getOrderDateTime()).isEqualTo(orderDateTime);
            assertThat(order.getStatus()).isEqualTo(status);
        }

        @Test
        @DisplayName("Deve lançar exceção quando o ID do restaurante for nulo")
        void deveLancarExcecaoQuandoRestaurantIdForNulo() {
            assertThatThrownBy(() -> new Order(1L, null, customerUuid, items, orderDateTime, StatusOrder.CREATED))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("restaurantId cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o UUID do cliente for nulo")
        void deveLancarExcecaoQuandoCustomerUuidForNulo() {
            assertThatThrownBy(() -> new Order(1L, restaurantId, null, items, orderDateTime, StatusOrder.CREATED))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("customerUuid cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando a lista de itens for nula")
        void deveLancarExcecaoQuandoItemsForNulo() {
            assertThatThrownBy(() -> new Order(1L, restaurantId, customerUuid, null, orderDateTime, StatusOrder.CREATED))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("items cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando a data e hora do pedido for nula")
        void deveLancarExcecaoQuandoOrderDateTimeForNulo() {
            assertThatThrownBy(() -> new Order(1L, restaurantId, customerUuid, items, null, StatusOrder.CREATED))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("orderDateTime cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o status for nulo")
        void deveLancarExcecaoQuandoStatusForNulo() {
            assertThatThrownBy(() -> new Order(1L, restaurantId, customerUuid, items, orderDateTime, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("status cannot be null.");
        }
    }

    @Nested
    @DisplayName("Transições de Status")
    class StatusTransitions {

        @Test
        @DisplayName("Deve confirmar o pedido mudando o status de CREATED para APPROVED")
        void deveConfirmarPedido() {
            // Arrange
            var order = new Order(1L, restaurantId, customerUuid, items, orderDateTime, StatusOrder.CREATED);

            // Act
            order.confirm();

            // Assert
            assertThat(order.getStatus()).isEqualTo(StatusOrder.APPROVED);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar confirmar um pedido que não está com status CREATED")
        void deveLancarExcecaoAoConfirmarPedidoComStatusIncorreto() {
            // Arrange
            var order = new Order(1L, restaurantId, customerUuid, items, orderDateTime, StatusOrder.APPROVED);

            // Act & Assert
            assertThatThrownBy(order::confirm)
                    .isInstanceOf(OperationNotAllowedException.class)
                    .hasMessageContaining("Order cannot be confirmed in this situation");
        }

        @Test
        @DisplayName("Deve alterar o status para PENDING_PAY quando o pedido está APPROVED")
        void deveAlterarStatusParaPendenteDePagamento() {
            // Arrange
            var order = new Order(1L, restaurantId, customerUuid, items, orderDateTime, StatusOrder.APPROVED);

            // Act
            order.pendingPay();

            // Assert
            assertThat(order.getStatus()).isEqualTo(StatusOrder.PENDING_PAY);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar alterar para PENDING_PAY com status diferente de APPROVED")
        void deveLancarExcecaoAoMudarParaPendenteDePagamentoComStatusIncorreto() {
            // Arrange
            var order = new Order(1L, restaurantId, customerUuid, items, orderDateTime, StatusOrder.CREATED);

            // Act & Assert
            assertThatThrownBy(order::pendingPay)
                    .isInstanceOf(OperationNotAllowedException.class)
                    .hasMessage("Order cannot be pending paid in this situation");
        }

        @Test
        @DisplayName("Deve alterar o status para PAYED quando o pedido está APPROVED")
        void devePagarPedido() {
            // Arrange
            var order = new Order(1L, restaurantId, customerUuid, items, orderDateTime, StatusOrder.APPROVED);

            // Act
            order.pay();

            // Assert
            assertThat(order.getStatus()).isEqualTo(StatusOrder.PAYED);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar pagar um pedido com status diferente de APPROVED")
        void deveLancarExcecaoAoPagarPedidoComStatusIncorreto() {
            // Arrange
            var order = new Order(1L, restaurantId, customerUuid, items, orderDateTime, StatusOrder.CREATED);

            // Act & Assert
            assertThatThrownBy(order::pay)
                    .isInstanceOf(OperationNotAllowedException.class)
                    .hasMessage("Order cannot be paid in this situation");
        }
    }

    @Nested
    @DisplayName("Manipulação de Itens e Total")
    class ItemAndTotalManipulation {

        @Test
        @DisplayName("Deve adicionar um item ao pedido")
        void deveAdicionarItemAoPedido() {
            // Arrange
            var order = new Order(1L, restaurantId, customerUuid, new ArrayList<>(), orderDateTime, StatusOrder.CREATED);
            var newItem = new MenuItem(3L, "New Item", BigDecimal.TEN, false, restaurantId);
            var newOrderItem = new OrderItem(newItem, BigDecimal.ONE);
            int initialSize = order.getItems().size();

            // Act
            order.addOrderItem(newOrderItem);

            // Assert
            assertThat(order.getItems()).hasSize(initialSize + 1);
            assertThat(order.getItems().get(initialSize)).isEqualTo(newOrderItem);
        }

        @Test
        @DisplayName("Deve retornar uma cópia da lista de itens para proteger o estado interno")
        void deveRetornarCopiaDaListaDeItens() {
            // Arrange
            var order = new Order(1L, restaurantId, customerUuid, items, orderDateTime, StatusOrder.CREATED);
            var itemsFromGetter = order.getItems();
            var newItem = new MenuItem(3L, "New Item", BigDecimal.TEN, false, restaurantId);
            var newOrderItem = new OrderItem(newItem, BigDecimal.ONE);

            // Act
            itemsFromGetter.add(newOrderItem);

            // Assert
            assertThat(order.getItems()).hasSize(items.size());
            assertThat(order.getItems()).isNotEqualTo(itemsFromGetter).doesNotContain(newOrderItem);
        }


        @Test
        @DisplayName("Deve calcular o total do pedido corretamente")
        void deveCalcularTotalDoPedido() {
            // Arrange
            var order = new Order(1L, restaurantId, customerUuid, items, orderDateTime, StatusOrder.CREATED);
            // Item 1: 10.00 * 1 = 10.00
            // Item 2: 20.00 * 2 = 40.00
            var expectedTotal = new BigDecimal("50.00");

            // Act
            var total = order.getTotal();

            // Assert
            assertThat(total).isEqualByComparingTo(expectedTotal);
        }
    }
}
