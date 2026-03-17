package br.com.fiap.restaurant.pedido.core.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para a classe OrderItem")
class OrderItemTest {

    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        menuItem = new MenuItem(1L, "Cheeseburger", new BigDecimal("25.50"), false, 1L);
    }

    @Nested
    @DisplayName("Construtores")
    class Constructors {

        @Test
        @DisplayName("Deve criar um item de pedido com sucesso usando o construtor principal")
        void deveCriarItemDePedidoComConstrutorPrincipal() {
            // Arrange
            var id = 1L;
            var quantity = new BigDecimal("2");
            var unitPrice = new BigDecimal("25.50");

            // Act
            var orderItem = new OrderItem(id, menuItem, quantity, unitPrice);

            // Assert
            assertThat(orderItem.getId()).isEqualTo(id);
            assertThat(orderItem.getMenuItem()).isEqualTo(menuItem);
            assertThat(orderItem.getQuantity()).isEqualTo(quantity);
            assertThat(orderItem.getUnitPrice()).isEqualTo(unitPrice);
        }

        @Test
        @DisplayName("Deve criar um item de pedido com sucesso usando o construtor de conveniência")
        void deveCriarItemDePedidoComConstrutorDeConveniencia() {
            // Arrange
            var quantity = new BigDecimal("3");

            // Act
            var orderItem = new OrderItem(menuItem, quantity);

            // Assert
            assertThat(orderItem.getId()).isNull();
            assertThat(orderItem.getMenuItem()).isEqualTo(menuItem);
            assertThat(orderItem.getQuantity()).isEqualTo(quantity);
            assertThat(orderItem.getUnitPrice()).isEqualTo(menuItem.getPrice());
        }

        @Test
        @DisplayName("Deve lançar NullPointerException quando o item de menu for nulo")
        void deveLancarExcecaoQuandoMenuItemForNulo() {
            // Arrange
            var quantity = BigDecimal.ONE;
            var unitPrice = BigDecimal.TEN;

            // Act & Assert
            assertThatThrownBy(() -> new OrderItem(1L, null, quantity, unitPrice))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("menuItem cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar NullPointerException quando a quantidade for nula")
        void deveLancarExcecaoQuandoQuantidadeForNula() {
            // Arrange
            var unitPrice = BigDecimal.TEN;

            // Act & Assert
            assertThatThrownBy(() -> new OrderItem(1L, menuItem, null, unitPrice))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("quantity cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar NullPointerException quando o preço unitário for nulo")
        void deveLancarExcecaoQuandoPrecoUnitarioForNulo() {
            // Arrange
            var quantity = BigDecimal.ONE;

            // Act & Assert
            assertThatThrownBy(() -> new OrderItem(1L, menuItem, quantity, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("quantity cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando a quantidade for zero")
        void deveLancarExcecaoQuandoQuantidadeForZero() {
            // Arrange
            var quantity = BigDecimal.ZERO;
            var unitPrice = BigDecimal.TEN;

            // Act & Assert
            assertThatThrownBy(() -> new OrderItem(1L, menuItem, quantity, unitPrice))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("quantity must be greater than zero");
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando a quantidade for negativa")
        void deveLancarExcecaoQuandoQuantidadeForNegativa() {
            // Arrange
            var quantity = new BigDecimal("-1");
            var unitPrice = BigDecimal.TEN;

            // Act & Assert
            assertThatThrownBy(() -> new OrderItem(1L, menuItem, quantity, unitPrice))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("quantity must be greater than zero");
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando o preço unitário for zero")
        void deveLancarExcecaoQuandoPrecoUnitarioForZero() {
            // Arrange
            var quantity = BigDecimal.ONE;
            var unitPrice = BigDecimal.ZERO;

            // Act & Assert
            assertThatThrownBy(() -> new OrderItem(1L, menuItem, quantity, unitPrice))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("unit price must be greater than zero");
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando o preço unitário for negativo")
        void deveLancarExcecaoQuandoPrecoUnitarioForNegativo() {
            // Arrange
            var quantity = BigDecimal.ONE;
            var unitPrice = new BigDecimal("-10");

            // Act & Assert
            assertThatThrownBy(() -> new OrderItem(1L, menuItem, quantity, unitPrice))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("unit price must be greater than zero");
        }
    }

    @Nested
    @DisplayName("Cálculos")
    class Calculations {

        @Test
        @DisplayName("Deve calcular o total corretamente")
        void deveCalcularTotalCorretamente() {
            // Arrange
            var quantity = new BigDecimal("2");
            var unitPrice = new BigDecimal("10.50");
            var orderItem = new OrderItem(1L, menuItem, quantity, unitPrice);
            var expectedTotal = new BigDecimal("21.00");

            // Act
            var total = orderItem.getTotal();

            // Assert
            assertThat(total).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("Deve calcular o total com arredondamento HALF_EVEN para cima")
        void deveCalcularTotalComArredondamentoParaCima() {
            // Arrange
            var quantity = BigDecimal.ONE;
            var unitPrice = new BigDecimal("10.115"); // Deve arredondar para 10.12
            var orderItem = new OrderItem(1L, menuItem, quantity, unitPrice);
            var expectedTotal = new BigDecimal("10.12");

            // Act
            var total = orderItem.getTotal();

            // Assert
            assertThat(total).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("Deve calcular o total com arredondamento HALF_EVEN para baixo")
        void deveCalcularTotalComArredondamentoParaBaixo() {
            // Arrange
            var quantity = BigDecimal.ONE;
            var unitPrice = new BigDecimal("10.125"); // Deve arredondar para 10.12
            var orderItem = new OrderItem(1L, menuItem, quantity, unitPrice);
            var expectedTotal = new BigDecimal("10.12");

            // Act
            var total = orderItem.getTotal();

            // Assert
            assertThat(total).isEqualTo(expectedTotal);
        }
    }
}
