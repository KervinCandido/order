package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import br.com.fiap.restaurant.pedido.core.inbound.OrderItemInput;
import br.com.fiap.restaurant.pedido.core.outbound.OrderOutput;
import br.com.fiap.restaurant.pedido.core.usecase.order.*;
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
import java.util.Optional;
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
    @Mock
    private FindOrderByIdUsecase findOrderByIdUsecase;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderController = new OrderController(createOrderUsecase, confirmOrderUseCase, pendingOrderUseCase, payOrderUseCase, findOrderByIdUsecase);
    }

    @Nested
    @DisplayName("Busca de Pedido por ID")
    class FindOrderById {

        @Test
        @DisplayName("Deve retornar o OrderOutput quando o pedido for encontrado")
        void dadoUmIdDePedidoExistente_quandoBuscarPorId_entaoDeveRetornarOOutputDoPedido() {
            // Given
            var orderId = 1L;
            var order = new Order(orderId, 1L, UUID.randomUUID(), new ArrayList<>(), LocalDateTime.now(), StatusOrder.DRAFT);
            given(findOrderByIdUsecase.findById(orderId)).willReturn(Optional.of(order));

            // When
            Optional<OrderOutput> result = orderController.findById(orderId);

            // Then
            then(findOrderByIdUsecase).should().findById(orderId);
            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo(orderId);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando o pedido não for encontrado")
        void dadoUmIdDePedidoInexistente_quandoBuscarPorId_entaoDeveRetornarOptionalVazio() {
            // Given
            var orderId = 1L;
            given(findOrderByIdUsecase.findById(orderId)).willReturn(Optional.empty());

            // When
            Optional<OrderOutput> result = orderController.findById(orderId);

            // Then
            then(findOrderByIdUsecase).should().findById(orderId);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar com ID nulo")
        void dadoUmIdDePedidoNulo_quandoBuscarPorId_entaoDeveLancarExcecao() {
            // When & Then
            assertThatThrownBy(() -> orderController.findById(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage(ORDER_ID_CANNOT_BE_NULL_MESSAGE);
            then(findOrderByIdUsecase).shouldHaveNoInteractions();
        }
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
            assertThatThrownBy(() -> new OrderController(null, confirmOrderUseCase, pendingOrderUseCase, payOrderUseCase, findOrderByIdUsecase))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("createOrderUsecase cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção se ConfirmOrderUseCase for nulo")
        void deveLancarExcecaoSeConfirmOrderUseCaseForNulo() {
            assertThatThrownBy(() -> new OrderController(createOrderUsecase, null, pendingOrderUseCase, payOrderUseCase, findOrderByIdUsecase))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("confirmOrderUseCase cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção se PendingOrderUseCase for nulo")
        void deveLancarExcecaoSePendingOrderUseCaseForNulo() {
            assertThatThrownBy(() -> new OrderController(createOrderUsecase, confirmOrderUseCase, null, payOrderUseCase, findOrderByIdUsecase))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("pendingOrderUseCase cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção se PayOrderUseCase for nulo")
        void deveLancarExcecaoSePayOrderUseCaseForNulo() {
            assertThatThrownBy(() -> new OrderController(createOrderUsecase, confirmOrderUseCase, pendingOrderUseCase, null, findOrderByIdUsecase))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("payOrderUseCase cannot be null.");
        }

        @Test
        @DisplayName("Deve lançar exceção se FindOrderByIdUsecase for nulo")
        void deveLancarExcecaoSeFindOrderByIdUsecaseForNulo() {
            assertThatThrownBy(() -> new OrderController(createOrderUsecase, confirmOrderUseCase, pendingOrderUseCase, payOrderUseCase, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("findOrderByIdUsecase cannot be null.");
        }
    }
}
