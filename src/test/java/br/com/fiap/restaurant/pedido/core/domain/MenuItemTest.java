package br.com.fiap.restaurant.pedido.core.domain;

import br.com.fiap.restaurant.pedido.core.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para a classe MenuItem")
class MenuItemTest {

    @Nested
    @DisplayName("Construtor")
    class Constructor {

        @Test
        @DisplayName("Deve criar um item de menu com sucesso")
        void deveCriarItemDeMenu() {
            // Arrange
            var id = 1L;
            var name = "Item Name";
            var price = BigDecimal.TEN;
            var restaurantOnly = false;
            var restaurantId = 1L;

            // Act
            var menuItem = new MenuItem(id, name, price, restaurantOnly, restaurantId);

            // Assert
            assertThat(menuItem.getId()).isEqualTo(id);
            assertThat(menuItem.getName()).isEqualTo(name);
            assertThat(menuItem.getUnitPrice()).isEqualTo(price);
            assertThat(menuItem.isRestaurantOnly()).isEqualTo(restaurantOnly);
            assertThat(menuItem.getRestaurantId()).isEqualTo(restaurantId);
        }

        @Test
        @DisplayName("Deve lançar exceção quando o ID do item de menu for nulo")
        void deveLancarExcecaoQuandoMenuIdNulo() {
            // Arrange
            var name = "Item Name";
            var price = BigDecimal.TEN;
            var restaurantOnly = false;
            var restaurantId = 1L;

            // Act & Assert
            assertThatThrownBy(() -> new MenuItem(null, name, price, restaurantOnly, restaurantId))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("menuItemId cannot be null");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o nome for nulo")
        void deveLancarExcecaoQuandoNomeNulo() {
            // Arrange
            var price = BigDecimal.TEN;
            var restaurantOnly = false;
            var restaurantId = 1L;

            // Act & Assert
            assertThatThrownBy(() -> new MenuItem(1L, null, price, restaurantOnly, restaurantId))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("name cannot be null");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o nome estiver em branco")
        void deveLancarExcecaoQuandoNomeEmBranco() {
            // Arrange
            var price = BigDecimal.TEN;
            var restaurantOnly = false;
            var restaurantId = 1L;

            // Act & Assert
            assertThatThrownBy(() -> new MenuItem(1L, " ", price, restaurantOnly, restaurantId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("name cannot be empty");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o preço for nulo")
        void deveLancarExcecaoQuandoPrecoNulo() {
            // Arrange
            var name = "Item Name";
            var restaurantOnly = false;
            var restaurantId = 1L;

            // Act & Assert
            assertThatThrownBy(() -> new MenuItem(1L, name, null, restaurantOnly, restaurantId))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("unitPrice cannot be null");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o preço for zero")
        void deveLancarExcecaoQuandoPrecoZero() {
            // Arrange
            var name = "Item Name";
            var price = BigDecimal.ZERO;
            var restaurantOnly = false;
            var restaurantId = 1L;

            // Act & Assert
            assertThatThrownBy(() -> new MenuItem(1L, name, price, restaurantOnly, restaurantId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("unitPrice must be greater than zero");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o preço for negativo")
        void deveLancarExcecaoQuandoPrecoNegativo() {
            // Arrange
            var name = "Item Name";
            var price = BigDecimal.valueOf(-1);
            var restaurantOnly = false;
            var restaurantId = 1L;

            // Act & Assert
            assertThatThrownBy(() -> new MenuItem(1L, name, price, restaurantOnly, restaurantId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("unitPrice must be greater than zero");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o ID do restaurante for nulo")
        void deveLancarExcecaoQuandoRestaurantIdNulo() {
            // Arrange
            var name = "Item Name";
            var price = BigDecimal.TEN;
            var restaurantOnly = false;

            // Act & Assert
            assertThatThrownBy(() -> new MenuItem(1L, name, price, restaurantOnly, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("restaurantId cannot be null");
        }

        @Test
        @DisplayName("Deve remover espaços em branco do nome")
        void deveRemoverEspacosEmBrancoDoNome() {
            // Arrange
            var nameWithWhitespace = "  Item Name  ";
            var expectedName = "Item Name";

            // Act
            var menuItem = new MenuItem(1L, nameWithWhitespace, BigDecimal.TEN, false, 1L);

            // Assert
            assertThat(menuItem.getName()).isEqualTo(expectedName);
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("Deve ser igual quando os IDs forem iguais")
        void deveSerIgualQuandoIdsForemIguais() {
            // Arrange
            var menuItem1 = new MenuItem(1L, "Item 1", BigDecimal.TEN, false, 1L);
            var menuItem2 = new MenuItem(1L, "Item 2", BigDecimal.ONE, true, 2L);

            // Act & Assert
            assertThat(menuItem1).isEqualTo(menuItem2).hasSameHashCodeAs(menuItem2);
        }

        @Test
        @DisplayName("Não deve ser igual quando os IDs forem diferentes")
        void naoDeveSerIgualQuandoIdsForemDiferentes() {
            // Arrange
            var menuItem1 = new MenuItem(1L, "Item", BigDecimal.TEN, false, 1L);
            var menuItem2 = new MenuItem(2L, "Item", BigDecimal.TEN, false, 1L);

            // Act & Assert
            assertThat(menuItem1).isNotEqualTo(menuItem2).doesNotHaveSameHashCodeAs(menuItem2);
        }

        @Test
        @DisplayName("Deve ser igual a si mesmo")
        void deveSerIgualASiMesmo() {
            // Arrange
            var menuItem = new MenuItem(1L, "Item", BigDecimal.TEN, false, 1L);
            Object menuItem2;
            menuItem2 = menuItem;
            // Act & Assert
            assertThat(menuItem).isEqualTo(menuItem2).hasSameHashCodeAs(menuItem2);
        }

        @Test
        @DisplayName("Não deve ser igual a nulo")
        void naoDeveSerIgualANulo() {
            // Arrange
            var menuItem = new MenuItem(1L, "Item", BigDecimal.TEN, false, 1L);
            Object obj = null;
            // Act & Assert
            assertThat(menuItem).isNotEqualTo(obj);
        }

        @Test
        @DisplayName("Não deve ser igual a um objeto de classe diferente")
        void naoDeveSerIgualAClasseDiferente() {
            // Arrange
            var menuItem = new MenuItem(1L, "Item", BigDecimal.TEN, false, 1L);
            var otherObject = new Object();

            // Act & Assert
            assertThat(menuItem).isNotEqualTo(otherObject);
        }
    }
}
