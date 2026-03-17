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
@DisplayName("Testes para o caso de uso DeleteMenuItemUsecase")
class DeleteMenuItemUsecaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;

    private DeleteMenuItemUsecase deleteMenuItemUsecase;

    @BeforeEach
    void setUp() {
        deleteMenuItemUsecase = new DeleteMenuItemUsecase(menuItemGateway);
    }

    @Nested
    @DisplayName("Cenários de execução")
    class ExecutionScenarios {

        @Test
        @DisplayName("Deve deletar um item de menu pelo ID com sucesso")
        void deveDeletarItemDeMenuPeloIdComSucesso() {
            // Given
            var menuItemId = 1L;

            // When
            deleteMenuItemUsecase.deleteById(menuItemId);

            // Then
            then(menuItemGateway).should().deleteById(menuItemId);
        }
    }

    @Nested
    @DisplayName("Validações de entrada e construtor")
    class ValidationScenarios {

        @Test
        @DisplayName("Deve lançar NullPointerException quando o ID do item de menu for nulo")
        void deveLancarExcecaoQuandoIdDoItemDeMenuForNulo() {
            // When & Then
            assertThatThrownBy(() -> deleteMenuItemUsecase.deleteById(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("menuItemId cannot be null");

            then(menuItemGateway).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar NullPointerException no construtor quando o gateway for nulo")
        void deveLancarExcecaoNoConstrutorQuandoGatewayForNulo() {
            // When & Then
            assertThatThrownBy(() -> new DeleteMenuItemUsecase(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("menuItemGateway cannot be null");
        }
    }
}
