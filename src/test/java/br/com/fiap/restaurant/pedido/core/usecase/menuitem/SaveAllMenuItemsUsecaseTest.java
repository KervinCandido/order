package br.com.fiap.restaurant.pedido.core.usecase.menuitem;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.pedido.core.inbound.MenuItemInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o caso de uso SaveAllMenuItemsUsecase")
class SaveAllMenuItemsUsecaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;

    private SaveAllMenuItemsUsecase saveAllMenuItemsUsecase;

    @Captor
    private ArgumentCaptor<List<MenuItem>> menuItemListCaptor;

    @BeforeEach
    void setUp() {
        saveAllMenuItemsUsecase = new SaveAllMenuItemsUsecase(menuItemGateway);
    }

    @Nested
    @DisplayName("Cenários de execução")
    class ExecutionScenarios {

        @Test
        @DisplayName("Deve salvar uma lista de itens de menu com sucesso")
        void deveSalvarListaDeItensComSucesso() {
            // Given
            var itemInput1 = new MenuItemInput(1L, "Item 1", BigDecimal.TEN, false, 1L);
            var itemInput2 = new MenuItemInput(2L, "Item 2", BigDecimal.ONE, true, 1L);
            var itemsInput = List.of(itemInput1, itemInput2);

            // When
            saveAllMenuItemsUsecase.save(itemsInput);

            // Then
            then(menuItemGateway).should().saveAll(menuItemListCaptor.capture());
            List<MenuItem> capturedItems = menuItemListCaptor.getValue();

            assertThat(capturedItems).hasSize(2);
            assertThat(capturedItems.get(0).getId()).isEqualTo(itemInput1.id());
            assertThat(capturedItems.get(0).getName()).isEqualTo(itemInput1.name());
            assertThat(capturedItems.get(1).getId()).isEqualTo(itemInput2.id());
            assertThat(capturedItems.get(1).getName()).isEqualTo(itemInput2.name());
        }

        @Test
        @DisplayName("Deve chamar o gateway com uma lista vazia quando a entrada for uma lista vazia")
        void deveProcessarListaVaziaCorretamente() {
            // Given
            var emptyList = Collections.<MenuItemInput>emptyList();

            // When
            saveAllMenuItemsUsecase.save(emptyList);

            // Then
            then(menuItemGateway).should().saveAll(menuItemListCaptor.capture());
            assertThat(menuItemListCaptor.getValue()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Validações de entrada e construtor")
    class ValidationScenarios {

        @Test
        @DisplayName("Deve lançar NullPointerException quando a lista de input for nula")
        void deveLancarExcecaoQuandoListaDeInputForNula() {
            // When & Then
            assertThatThrownBy(() -> saveAllMenuItemsUsecase.save(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("itemsInput cannot be null");

            then(menuItemGateway).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar NullPointerException no construtor quando o gateway for nulo")
        void deveLancarExcecaoNoConstrutorQuandoGatewayForNulo() {
            // When & Then
            assertThatThrownBy(() -> new SaveAllMenuItemsUsecase(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("menuItemGateway cannot be null");
        }
    }
}
