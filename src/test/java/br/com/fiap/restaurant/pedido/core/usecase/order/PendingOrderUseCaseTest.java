package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.exception.BusinessException;
import br.com.fiap.restaurant.pedido.core.exception.InvalidOrderStateException;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
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
@DisplayName("Testes para o caso de uso PendingOrderUseCase")
class PendingOrderUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    private PendingOrderUseCase pendingOrderUseCase;

    @BeforeEach
    void setUp() {
        pendingOrderUseCase = new PendingOrderUseCase(orderGateway);
    }

    @Nested
    @DisplayName("Cenários de execução")
    class ExecutionScenarios {

        @Test
        @DisplayName("Deve alterar o status do pedido para pendente de pagamento com sucesso")
        void deveAlterarStatusParaPendenteComSucesso() {
            // Given
            var orderId = 1L;
            var order = new Order(orderId, 1L, UUID.randomUUID(), new ArrayList<>(), LocalDateTime.now(), StatusOrder.CREATED);
            given(orderGateway.findById(orderId)).willReturn(Optional.of(order));

            // When
            pendingOrderUseCase.pendingOrderById(orderId);

            // Then
            assertThat(order.getStatus()).isEqualTo(StatusOrder.PENDING_PAY);
            then(orderGateway).should().save(order);
        }

        @Test
        @DisplayName("Deve lançar exceção quando o pedido não for encontrado")
        void deveLancarExcecaoQuandoPedidoNaoForEncontrado() {
            // Given
            var orderId = 1L;
            given(orderGateway.findById(orderId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> pendingOrderUseCase.pendingOrderById(orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Order not found");

            then(orderGateway).should().findById(orderId);
            then(orderGateway).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve propagar exceção quando a transição de status para pendente não for permitida")
        void devePropagarExcecaoQuandoTransicaoDeStatusNaoForPermitida() {
            // Given
            var orderId = 1L;
            // Um pedido com status CREATED não pode ser movido para PENDING_PAY diretamente
            var order = new Order(orderId, 1L, UUID.randomUUID(), new ArrayList<>(), LocalDateTime.now(), StatusOrder.DRAFT);
            given(orderGateway.findById(orderId)).willReturn(Optional.of(order));

            // When & Then
            assertThatThrownBy(() -> pendingOrderUseCase.pendingOrderById(orderId))
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessage("Order cannot be pending paid in this situation");

            then(orderGateway).should().findById(orderId);
            then(orderGateway).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("Validações de entrada e construtor")
    class ValidationScenarios {

        @Test
        @DisplayName("Deve lançar NullPointerException quando o ID do pedido for nulo")
        void deveLancarExcecaoQuandoIdDoPedidoForNulo() {
            // When & Then
            assertThatThrownBy(() -> pendingOrderUseCase.pendingOrderById(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("orderId cannot be null");

            then(orderGateway).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar NullPointerException no construtor quando o gateway for nulo")
        void deveLancarExcecaoNoConstrutorQuandoGatewayForNulo() {
            // When & Then
            assertThatThrownBy(() -> new PendingOrderUseCase(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("OrderGateway cannot be null");
        }
    }
}
