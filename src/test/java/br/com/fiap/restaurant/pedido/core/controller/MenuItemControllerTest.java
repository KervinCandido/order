package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.inbound.MenuItemInput;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.DeleteAllMenuItemsByRestaurantIdUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.DeleteMenuItemUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.SaveAllMenuItemsUsecase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para a classe MenuItemController")
class MenuItemControllerTest {

    @Mock
    private SaveAllMenuItemsUsecase saveAllMenuItemsUsecase;
    @Mock
    private DeleteAllMenuItemsByRestaurantIdUsecase deleteAllMenuItemsByRestaurantIdUsecase;
    @Mock
    private DeleteMenuItemUsecase deleteMenuItemUsecase;

    private MenuItemController menuItemController;

    @BeforeEach
    void setUp() {
        menuItemController = new MenuItemController(saveAllMenuItemsUsecase, deleteAllMenuItemsByRestaurantIdUsecase, deleteMenuItemUsecase);
    }

    @Nested
    @DisplayName("Criação de Itens de Menu")
    class CreateMenuItems {

        @Test
        @DisplayName("Deve chamar o caso de uso para salvar todos os itens de menu")
        void deveChamarCasoDeUsoParaSalvarItens() {
            // Given
            var input = List.of(new MenuItemInput(1L, "Item", BigDecimal.TEN, false, 1L));

            // When
            menuItemController.createMenuItems(input);

            // Then
            then(saveAllMenuItemsUsecase).should().save(input);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar criar com input nulo")
        void deveLancarExcecaoAoCriarComInputNulo() {
            // When & Then
            assertThatThrownBy(() -> menuItemController.createMenuItems(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("updateAllMenuItemsInput cannot be null");
            then(saveAllMenuItemsUsecase).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Atualização de Item de Menu")
    class UpdateMenuItem {

        @Test
        @DisplayName("Deve chamar o caso de uso para salvar um único item de menu")
        void deveChamarCasoDeUsoParaSalvarItemUnico() {
            // Given
            var menuInput = new MenuItemInput(1L, "Item", BigDecimal.TEN, false, 1L);

            // When
            menuItemController.updateMenuItem(menuInput);

            // Then
            then(saveAllMenuItemsUsecase).should().save(List.of(menuInput));
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar atualizar com input nulo")
        void deveLancarExcecaoAoAtualizarComInputNulo() {
            // When & Then
            assertThatThrownBy(() -> menuItemController.updateMenuItem(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("menuItemInput cannot be null");
            then(saveAllMenuItemsUsecase).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Exclusão de Itens de Menu")
    class DeleteMenuItems {

        @Test
        @DisplayName("Deve chamar o caso de uso para deletar um item de menu por ID")
        void deveChamarCasoDeUsoParaDeletarItemPorId() {
            // Given
            var menuItemId = 1L;

            // When
            menuItemController.deleteMenuItem(menuItemId);

            // Then
            then(deleteMenuItemUsecase).should().deleteById(menuItemId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar deletar com ID de item nulo")
        void deveLancarExcecaoAoDeletarComIdNulo() {
            // When & Then
            assertThatThrownBy(() -> menuItemController.deleteMenuItem(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("menuItemInput cannot be null");
            then(deleteMenuItemUsecase).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve chamar o caso de uso para deletar por ID de restaurante")
        void deveChamarCasoDeUsoParaDeletarPorIdRestaurante() {
            // Given
            var restaurantId = 1L;

            // When
            menuItemController.deleteByRestaurantId(restaurantId);

            // Then
            then(deleteAllMenuItemsByRestaurantIdUsecase).should().deleteByRestaurantId(restaurantId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar deletar com ID de restaurante nulo")
        void deveLancarExcecaoAoDeletarComIdRestauranteNulo() {
            // When & Then
            assertThatThrownBy(() -> menuItemController.deleteByRestaurantId(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("restaurantId cannot be null");
            then(deleteAllMenuItemsByRestaurantIdUsecase).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Atualização de todos os Itens de um Restaurante")
    class UpdateAllMenuItems {

        @Test
        @DisplayName("Deve deletar e depois salvar os itens, na ordem correta")
        void deveDeletarEDepoisSalvar() {
            // Given
            var restaurantId = 1L;
            var menuItemsInput = List.of(new MenuItemInput(1L, "New Item", BigDecimal.TEN, false, restaurantId));
            InOrder inOrder = inOrder(deleteAllMenuItemsByRestaurantIdUsecase, saveAllMenuItemsUsecase);

            // When
            menuItemController.updateAllMenuItemsOfRestaurant(restaurantId, menuItemsInput);

            // Then
            then(deleteAllMenuItemsByRestaurantIdUsecase).should(inOrder).deleteByRestaurantId(restaurantId);
            then(saveAllMenuItemsUsecase).should(inOrder).save(menuItemsInput);
        }

        @Test
        @DisplayName("Deve lançar exceção se o ID do restaurante for nulo")
        void deveLancarExcecaoSeIdRestauranteForNulo() {
            // When & Then
            List<MenuItemInput> menuItemsInput = List.of();
            assertThatThrownBy(() -> menuItemController.updateAllMenuItemsOfRestaurant(null, menuItemsInput))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("restaurantId cannot be null");
        }

        @Test
        @DisplayName("Deve lançar exceção se a lista de itens for nula")
        void deveLancarExcecaoSeListaDeItensForNula() {
            // When & Then
            assertThatThrownBy(() -> menuItemController.updateAllMenuItemsOfRestaurant(1L, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("menuItemsInput cannot be null");
        }
    }

    @Nested
    @DisplayName("Validações do Construtor")
    class ConstructorValidation {

        @Test
        @DisplayName("Deve lançar exceção se SaveAllMenuItemsUsecase for nulo")
        void deveLancarExcecaoSeSaveAllMenuItemsUsecaseForNulo() {
            assertThatThrownBy(() -> new MenuItemController(null, deleteAllMenuItemsByRestaurantIdUsecase, deleteMenuItemUsecase))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("updateAllMenuItemsUsecase cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção se DeleteAllMenuItemsByRestaurantIdUsecase for nulo")
        void deveLancarExcecaoSeDeleteAllMenuItemsByRestaurantIdUsecaseForNulo() {
            assertThatThrownBy(() -> new MenuItemController(saveAllMenuItemsUsecase, null, deleteMenuItemUsecase))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("deleteAllMenuItemsByRestaurantIdUsecase cannot be null");
        }

        @Test
        @DisplayName("Deve lançar exceção se DeleteMenuItemUsecase for nulo")
        void deveLancarExcecaoSeDeleteMenuItemUsecaseForNulo() {
            assertThatThrownBy(() -> new MenuItemController(saveAllMenuItemsUsecase, deleteAllMenuItemsByRestaurantIdUsecase, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("deleteMenuItemUsecase cannot be null");
        }
    }
}
