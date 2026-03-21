package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import br.com.fiap.restaurant.pedido.core.inbound.OrderItemInput;
import br.com.fiap.restaurant.pedido.core.outbound.OrderOutput;
import br.com.fiap.restaurant.pedido.core.usecase.order.ConfirmOrderUseCase;
import br.com.fiap.restaurant.pedido.core.usecase.order.CreateOrderUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.order.PayOrderUseCase;
import br.com.fiap.restaurant.pedido.core.usecase.order.PendingOrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static br.com.fiap.restaurant.pedido.core.controller.OrderController.ORDER_ID_CANNOT_BE_NULL_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para a classe OrderController")
class OrderControllerTest {

    @Mock
    private CreateOrderUsecase createOrderUsecase;
    @Mock
    private ConfirmOrderUseCase confirmOrderUseCase;
    @Mock
    private PendingOrderUseCase pendingOrderUseCase;
    @Mock
    private PayOrderUseCase payOrderUseCase;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderController = new OrderController(createOrderUsecase, confirmOrderUseCase, pendingOrderUseCase, payOrderUseCase);
    }

    @Nested
    @DisplayName("Criação de Pedido")
    class CreateOrder {

        @Test
        @DisplayName("Deve criar um pedido e retornar o output formatado")
        void deveCriarPedidoERetornarOutput() {
            // Given
            var input = new CreateOrderInput(1L, List.of(new OrderItemInput(1L, BigDecimal.ONE)));
            var order = new Order(1L, 1L, UUID.randomUUID(), new ArrayList<>(), LocalDateTime.now(), StatusOrder.DRAFT);
            given(createOrderUsecase.create(input)).willReturn(order);

            // When
            OrderOutput result = orderController.create(input);

            // Then
            then(createOrderUsecase).should().create(input);
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(order.getId());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar criar pedido com input nulo")
        void deveLancarExcecaoAoCriarComInputNulo() {
            // When & Then
            assertThatThrownBy(() -> orderController.create(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("createOrderInput cannot be null.");
            then(createOrderUsecase).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Confirmação de Pedido")
    class ConfirmOrder {

        @Test
        @DisplayName("Deve chamar o caso de uso de confirmação com o ID correto")
        void deveChamarCasoDeUsoDeConfirmacao() {
            // Given
            var orderId = 1L;

            // When
            orderController.confirm(orderId);

            // Then
            then(confirmOrderUseCase).should().confirmOrderBy(orderId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao confirmar com ID nulo")
        void deveLancarExcecaoAoConfirmarComIdNulo() {
            // When & Then
            assertThatThrownBy(() -> orderController.confirm(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage(ORDER_ID_CANNOT_BE_NULL_MESSAGE);
            then(confirmOrderUseCase).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Pagamento de Pedido")
    class PayOrder {

        @Test
        @DisplayName("Deve chamar o caso de uso de pagamento com o ID correto")
        void deveChamarCasoDeUsoDePagamento() {
            // Given
            var orderId = 1L;

            // When
            orderController.payOrder(orderId);

            // Then
            then(payOrderUseCase).should().payOrderById(orderId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao pagar com ID nulo")
        void deveLancarExcecaoAoPagarComIdNulo() {
            // When & Then
            assertThatThrownBy(() -> orderController.payOrder(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage(ORDER_ID_CANNOT_BE_NULL_MESSAGE);
            then(payOrderUseCase).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Pedido Pendente de Pagamento")
    class PendingPaymentOrder {

        @Test
        @DisplayName("Deve chamar o caso de uso de pendência de pagamento com o ID correto")
        void deveChamarCasoDeUsoDePendenciaDePagamento() {
            // Given
            var orderId = 1L;

            // When
            orderController.pendingPaymentOrder(orderId);

            // Then
            then(pendingOrderUseCase).should().pendingOrderById(orderId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao marcar como pendente com ID nulo")
        void deveLancarExcecaoAoMarcarPendenteComIdNulo() {
            // When & Then
            assertThatThrownBy(() -> orderController.pendingPaymentOrder(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage(ORDER_ID_CANNOT_BE_NULL_MESSAGE);
            then(pendingOrderUseCase).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Validações do Construtor")
    class ConstructorValidation {

        @Test
        @DisplayName("Deve lançar exceção se CreateOrderUsecase for nulo")
        void deveLancarExcecaoSeCreateOrderUsecaseForNulo() {
            assertThatThrownBy(() -> new OrderController(null, confirmOrderUseCase, pendingOrderUseCase, payOrderUseCase))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("createOrderUsecase cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção se ConfirmOrderUseCase for nulo")
        void deveLancarExcecaoSeConfirmOrderUseCaseForNulo() {
            assertThatThrownBy(() -> new OrderController(createOrderUsecase, null, pendingOrderUseCase, payOrderUseCase))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("confirmOrderUseCase cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção se PendingOrderUseCase for nulo")
        void deveLancarExcecaoSePendingOrderUseCaseForNulo() {
            assertThatThrownBy(() -> new OrderController(createOrderUsecase, confirmOrderUseCase, null, payOrderUseCase))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("pendingOrderUseCase cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção se PayOrderUseCase for nulo")
        void deveLancarExcecaoSePayOrderUseCaseForNulo() {
            assertThatThrownBy(() -> new OrderController(createOrderUsecase, confirmOrderUseCase, pendingOrderUseCase, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("payOrderUseCase cannot be null.");
        }
    }
}
