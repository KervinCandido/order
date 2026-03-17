package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.exception.MenuItemNotFoundException;
import br.com.fiap.restaurant.pedido.core.exception.UserNotAuthenticatedException;
import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import br.com.fiap.restaurant.pedido.core.inbound.OrderItemInput;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o caso de uso CreateOrderUsecase")
class CreateOrderUsecaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;
    @Mock
    private OrderGateway orderGateway;
    @Mock
    private LoggedUserGateway loggedUserGateway;

    private CreateOrderUsecase createOrderUsecase;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @BeforeEach
    void setUp() {
        createOrderUsecase = new CreateOrderUsecase(menuItemGateway, orderGateway, loggedUserGateway);
    }

    @Nested
    @DisplayName("Cenários de execução")
    class ExecutionScenarios {

        @Test
        @DisplayName("Deve criar um pedido com sucesso")
        void deveCriarUmPedidoComSucesso() {
            // Given
            var restaurantId = 1L;
            var customerUuid = UUID.randomUUID();
            var orderItemInput1 = new OrderItemInput(10L, new BigDecimal("2"));
            var orderItemInput2 = new OrderItemInput(20L, new BigDecimal("1"));
            var input = new CreateOrderInput(restaurantId, List.of(orderItemInput1, orderItemInput2));

            var menuItem1 = new MenuItem(10L, "Item 1", new BigDecimal("15.00"), false, restaurantId);
            var menuItem2 = new MenuItem(20L, "Item 2", new BigDecimal("25.00"), false, restaurantId);

            given(loggedUserGateway.getCurrentUser()).willReturn(Optional.of(customerUuid));
            given(menuItemGateway.findAllById(Set.of(10L, 20L))).willReturn(List.of(menuItem1, menuItem2));
            given(orderGateway.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

            // When
            Order result = createOrderUsecase.create(input);

            // Then
            then(orderGateway).should().save(orderCaptor.capture());
            Order capturedOrder = orderCaptor.getValue();

            assertThat(result).isEqualTo(capturedOrder);
            assertThat(capturedOrder.getRestaurantId()).isEqualTo(restaurantId);
            assertThat(capturedOrder.getCustomerUuid()).isEqualTo(customerUuid);
            assertThat(capturedOrder.getStatus()).isEqualTo(StatusOrder.CREATED);
            assertThat(capturedOrder.getItems()).hasSize(2);
            assertThat(capturedOrder.getItems().getFirst().getMenuItem().getId()).isEqualTo(menuItem1.getId());
            assertThat(capturedOrder.getItems().getFirst().getQuantity()).isEqualTo(orderItemInput1.quantity());
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não estiver autenticado")
        void deveLancarExcecaoQuandoUsuarioNaoEstiverAutenticado() {
            // Given
            var input = new CreateOrderInput(1L, List.of());
            given(loggedUserGateway.getCurrentUser()).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> createOrderUsecase.create(input))
                    .isInstanceOf(UserNotAuthenticatedException.class);

            then(menuItemGateway).shouldHaveNoInteractions();
            then(orderGateway).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar exceção quando um item de menu não for encontrado")
        void deveLancarExcecaoQuandoUmItemDeMenuNaoForEncontrado() {
            // Given
            var restaurantId = 1L;
            var customerUuid = UUID.randomUUID();
            var orderItemInput1 = new OrderItemInput(10L, new BigDecimal("1")); // Found
            var orderItemInput2 = new OrderItemInput(99L, new BigDecimal("1")); // Not found
            var input = new CreateOrderInput(restaurantId, List.of(orderItemInput1, orderItemInput2));

            var menuItem1 = new MenuItem(10L, "Item 1", new BigDecimal("15.00"), false, restaurantId);

            given(loggedUserGateway.getCurrentUser()).willReturn(Optional.of(customerUuid));
            given(menuItemGateway.findAllById(Set.of(10L, 99L))).willReturn(List.of(menuItem1));

            // When & Then
            assertThatThrownBy(() -> createOrderUsecase.create(input))
                    .isInstanceOf(MenuItemNotFoundException.class)
                    .hasMessageContaining("item(s) not found [99]");

            then(orderGateway).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar exceção quando um item de menu pertence a outro restaurante")
        void deveLancarExcecaoQuandoUmItemDeMenuPertenceAOutroRestaurante() {
            // Given
            var restaurantId = 1L;
            var otherRestaurantId = 2L;
            var customerUuid = UUID.randomUUID();
            var orderItemInput1 = new OrderItemInput(10L, new BigDecimal("1"));
            var orderItemInput2 = new OrderItemInput(20L, new BigDecimal("1")); // Belongs to other restaurant
            var input = new CreateOrderInput(restaurantId, List.of(orderItemInput1, orderItemInput2));

            var menuItem1 = new MenuItem(10L, "Item 1", new BigDecimal("15.00"), false, restaurantId);
            var menuItem2 = new MenuItem(20L, "Item 2", new BigDecimal("25.00"), false, otherRestaurantId);

            given(loggedUserGateway.getCurrentUser()).willReturn(Optional.of(customerUuid));
            given(menuItemGateway.findAllById(Set.of(10L, 20L))).willReturn(List.of(menuItem1, menuItem2));

            // When & Then
            assertThatThrownBy(() -> createOrderUsecase.create(input))
                    .isInstanceOf(MenuItemNotFoundException.class)
                    .hasMessageContaining("item(s) not found [20]");

            then(orderGateway).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Validações de entrada e construtor")
    class ValidationScenarios {

        @Test
        @DisplayName("Deve lançar NullPointerException quando o input for nulo")
        void deveLancarExcecaoQuandoInputForNulo() {
            // When & Then
            assertThatThrownBy(() -> createOrderUsecase.create(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("createOrderInput cannot be null.");
            then(loggedUserGateway).shouldHaveNoInteractions();
            then(menuItemGateway).shouldHaveNoInteractions();
            then(orderGateway).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar NullPointerException no construtor quando MenuItemGateway for nulo")
        void deveLancarExcecaoNoConstrutorQuandoMenuItemGatewayForNulo() {
            assertThatThrownBy(() -> new CreateOrderUsecase(null, orderGateway, loggedUserGateway))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("menuItemGateway cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar NullPointerException no construtor quando OrderGateway for nulo")
        void deveLancarExcecaoNoConstrutorQuandoOrderGatewayForNulo() {
            assertThatThrownBy(() -> new CreateOrderUsecase(menuItemGateway, null, loggedUserGateway))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("orderGateway cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar NullPointerException no construtor quando LoggedUserGateway for nulo")
        void deveLancarExcecaoNoConstrutorQuandoLoggedUserGatewayForNulo() {
            assertThatThrownBy(() -> new CreateOrderUsecase(menuItemGateway, orderGateway, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("loggedUserGateway cannot be null.");
        }
    }
}
