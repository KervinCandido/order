package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.exception.BusinessException;
import br.com.fiap.restaurant.pedido.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.pedido.core.exception.UserNotAuthenticatedException;
import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
import br.com.fiap.restaurant.pedido.core.gateway.PublisherGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o caso de uso ConfirmOrderUseCase")
class ConfirmOrderUseCaseTest {

    @Mock
    private LoggedUserGateway loggedUserGateway;
    @Mock
    private OrderGateway orderGateway;
    @Mock
    private PublisherGateway<Order> confirmOrderPublisher;

    private ConfirmOrderUseCase confirmOrderUseCase;

    @BeforeEach
    void setUp() {
        confirmOrderUseCase = new ConfirmOrderUseCase(loggedUserGateway, orderGateway, confirmOrderPublisher);
    }

    @Nested
    @DisplayName("Cenários de execução")
    class ExecutionScenarios {

        @Test
        @DisplayName("Deve confirmar um pedido com sucesso")
        void deveConfirmarPedidoComSucesso() {
            // Given
            var orderId = 1L;
            var customerUuid = UUID.randomUUID();
            var order = new Order(orderId, 1L, customerUuid, new ArrayList<>(), LocalDateTime.now(), StatusOrder.CREATED);

            given(orderGateway.findById(orderId)).willReturn(Optional.of(order));
            given(loggedUserGateway.getCurrentUser()).willReturn(Optional.of(customerUuid));

            // When
            confirmOrderUseCase.confirmOrderBy(orderId);

            // Then
            assertThat(order.getStatus()).isEqualTo(StatusOrder.APPROVED);
            then(orderGateway).should().save(order);
            then(confirmOrderPublisher).should().publish(order);
        }

        @Test
        @DisplayName("Deve lançar exceção quando o pedido não for encontrado")
        void deveLancarExcecaoQuandoPedidoNaoForEncontrado() {
            // Given
            var orderId = 1L;
            given(orderGateway.findById(orderId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> confirmOrderUseCase.confirmOrderBy(orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Order not found");

            then(loggedUserGateway).shouldHaveNoInteractions();
            then(confirmOrderPublisher).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar exceção quando o usuário não estiver autenticado")
        void deveLancarExcecaoQuandoUsuarioNaoEstiverAutenticado() {
            // Given
            var orderId = 1L;
            var customerUuid = UUID.randomUUID();
            var order = new Order(orderId, 1L, customerUuid, new ArrayList<>(), LocalDateTime.now(), StatusOrder.CREATED);

            given(orderGateway.findById(orderId)).willReturn(Optional.of(order));
            given(loggedUserGateway.getCurrentUser()).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> confirmOrderUseCase.confirmOrderBy(orderId))
                    .isInstanceOf(UserNotAuthenticatedException.class);

            then(orderGateway).should().findById(orderId);
            then(orderGateway).shouldHaveNoMoreInteractions();
            then(confirmOrderPublisher).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar confirmar pedido de outro cliente")
        void deveLancarExcecaoAoTentarConfirmarPedidoDeOutroCliente() {
            // Given
            var orderId = 1L;
            var orderCustomerUuid = UUID.randomUUID();
            var loggedUserUuid = UUID.randomUUID();
            var order = new Order(orderId, 1L, orderCustomerUuid, new ArrayList<>(), LocalDateTime.now(), StatusOrder.CREATED);

            given(orderGateway.findById(orderId)).willReturn(Optional.of(order));
            given(loggedUserGateway.getCurrentUser()).willReturn(Optional.of(loggedUserUuid));

            // When & Then
            assertThatThrownBy(() -> confirmOrderUseCase.confirmOrderBy(orderId))
                    .isInstanceOf(OperationNotAllowedException.class)
                    .hasMessage("Current user cannot confirm this order");

            then(orderGateway).should().findById(orderId);
            then(orderGateway).shouldHaveNoMoreInteractions();
            then(confirmOrderPublisher).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Validações de entrada e construtor")
    class ValidationScenarios {

        @Test
        @DisplayName("Deve lançar NullPointerException quando o ID do pedido for nulo")
        void deveLancarExcecaoQuandoIdDoPedidoForNulo() {
            // When & Then
            assertThatThrownBy(() -> confirmOrderUseCase.confirmOrderBy(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("orderId cannot be null");

            then(orderGateway).shouldHaveNoInteractions();
            then(loggedUserGateway).shouldHaveNoInteractions();
            then(confirmOrderPublisher).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar NullPointerException no construtor quando LoggedUserGateway for nulo")
        void deveLancarExcecaoNoConstrutorQuandoLoggedUserGatewayForNulo() {
            // When & Then
            assertThatThrownBy(() -> new ConfirmOrderUseCase(null, orderGateway, confirmOrderPublisher))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("LoggedUserGateway cannot be null");
        }

        @Test
        @DisplayName("Deve lançar NullPointerException no construtor quando OrderGateway for nulo")
        void deveLancarExcecaoNoConstrutorQuandoOrderGatewayForNulo() {
            // When & Then
            assertThatThrownBy(() -> new ConfirmOrderUseCase(loggedUserGateway, null, confirmOrderPublisher))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("OrderGateway cannot be null");
        }

        @Test
        @DisplayName("Deve lançar NullPointerException no construtor quando PublisherGateway for nulo")
        void deveLancarExcecaoNoConstrutorQuandoPublisherGatewayForNulo() {
            // When & Then
            assertThatThrownBy(() -> new ConfirmOrderUseCase(loggedUserGateway, orderGateway, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ConfirmOrderPublisher cannot be null");
        }
    }
}
