package br.com.fiap.restaurant.pedido.core.usecase.menuitem;

import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o caso de uso DeleteAllMenuItemsByRestaurantIdUsecase")
class DeleteAllMenuItemsByRestaurantIdUsecaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;

    private DeleteAllMenuItemsByRestaurantIdUsecase deleteAllMenuItemsByRestaurantIdUsecase;

    @BeforeEach
    void setUp() {
        deleteAllMenuItemsByRestaurantIdUsecase = new DeleteAllMenuItemsByRestaurantIdUsecase(menuItemGateway);
    }

    @Nested
    @DisplayName("Cenários de execução")
    class ExecutionScenarios {

        @Test
        @DisplayName("Deve deletar todos os itens de menu pelo ID do restaurante com sucesso")
        void deveDeletarTodosOsItensDeMenuPeloIdDoRestauranteComSucesso() {
            // Given
            var restaurantId = 1L;

            // When
            deleteAllMenuItemsByRestaurantIdUsecase.deleteByRestaurantId(restaurantId);

            // Then
            then(menuItemGateway).should().deleteAllByRestaurantId(restaurantId);
        }
    }

    @Nested
    @DisplayName("Validações de entrada e construtor")
    class ValidationScenarios {

        @Test
        @DisplayName("Deve lançar NullPointerException quando o ID do restaurante for nulo")
        void deveLancarExcecaoQuandoIdDoRestauranteForNulo() {
            // When & Then
            assertThatThrownBy(() -> deleteAllMenuItemsByRestaurantIdUsecase.deleteByRestaurantId(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("restaurantId cannot be null");

            then(menuItemGateway).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar NullPointerException no construtor quando o gateway for nulo")
        void deveLancarExcecaoNoConstrutorQuandoGatewayForNulo() {
            // When & Then
            assertThatThrownBy(() -> new DeleteAllMenuItemsByRestaurantIdUsecase(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("menuItemGateway cannot be null");
        }
    }
}
