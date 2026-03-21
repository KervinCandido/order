package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.domain.pagination.Page;
import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import br.com.fiap.restaurant.pedido.core.inbound.OrderItemInput;
import br.com.fiap.restaurant.pedido.core.outbound.OrderOutput;
import br.com.fiap.restaurant.pedido.core.presenter.OrderPresenter;
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
import java.util.Set;
import java.util.UUID;

import static br.com.fiap.restaurant.pedido.core.controller.OrderController.ORDER_ID_CANNOT_BE_NULL_MESSAGE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para a classe OrderController")
class OrderControllerTest {

    @Mock
    private OrderUsecaseFacade orderUsecaseFacade;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderController = new OrderController(orderUsecaseFacade);
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
            given(orderUsecaseFacade.findOrderById(orderId)).willReturn(Optional.of(order));

            // When
            Optional<OrderOutput> result = orderController.findById(orderId);

            // Then
            then(orderUsecaseFacade).should().findOrderById(orderId);
            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo(orderId);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando o pedido não for encontrado")
        void dadoUmIdDePedidoInexistente_quandoBuscarPorId_entaoDeveRetornarOptionalVazio() {
            // Given
            var orderId = 1L;
            given(orderUsecaseFacade.findOrderById(orderId)).willReturn(Optional.empty());

            // When
            Optional<OrderOutput> result = orderController.findById(orderId);

            // Then
            then(orderUsecaseFacade).should().findOrderById(orderId);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar com ID nulo")
        void dadoUmIdDePedidoNulo_quandoBuscarPorId_entaoDeveLancarExcecao() {
            // When & Then
            assertThatThrownBy(() -> orderController.findById(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage(ORDER_ID_CANNOT_BE_NULL_MESSAGE);
            then(orderUsecaseFacade).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Busca de Pedidos por Usuário Atual")
    class FindOrderByCurrentUser {

        private Order order;

        @BeforeEach
        void setUp() {
            this.order = new Order(1L, 1L, UUID.randomUUID(), new ArrayList<>(), LocalDateTime.now(), StatusOrder.DRAFT);
        }

        @Test
        @DisplayName("Deve retornar a página de pedidos quando o status for informado")
        void deveRetornarPaginaDePedidosQuandoStatusInformado() {
            // Given
            var status = Set.of(StatusOrder.DRAFT);
            var pageNumber = 0;
            var pageSize = 10;
            var page = new Page<>(pageNumber, pageSize, 1, List.of(order));
            var outputPage = new Page<>(pageNumber, pageSize, 1, List.of(OrderPresenter.toOutput(order)));
            given(orderUsecaseFacade.findOrderByCurrentUser(status, pageNumber, pageSize)).willReturn(page);

            // When
            var result = orderController.findOrderByCurrentUser(status, pageNumber, pageSize);

            // Then
            then(orderUsecaseFacade).should().findOrderByCurrentUser(status, pageNumber, pageSize);
            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(pageNumber);
            assertThat(result.pageSize()).isEqualTo(pageSize);
            assertThat(result.totalElements()).isOne();
            assertThat(result.totalPages()).isOne();
            assertThat(result.content()).isNotNull().hasSize(1);
            assertThat(result).isEqualTo(outputPage);
        }

        @Test
        @DisplayName("Deve retornar a página de pedidos com status vazio quando o status for null")
        void deveRetornarPaginaDePedidosComStatusVazioQuandoStatusVazio() {
            // Given
            var pageNumber = 0;
            var pageSize = 10;
            var page = new Page<>(pageNumber, pageSize, 1, List.of(order));
            var outputPage = new Page<>(pageNumber, pageSize, 1, List.of(OrderPresenter.toOutput(order)));
            given(orderUsecaseFacade.findOrderByCurrentUser(Set.of(), pageNumber, pageSize)).willReturn(page);

            // When
            var result = orderController.findOrderByCurrentUser(null, pageNumber, pageSize);

            // Then
            then(orderUsecaseFacade).should().findOrderByCurrentUser(Set.of(), pageNumber, pageSize);
            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(pageNumber);
            assertThat(result.pageSize()).isEqualTo(pageSize);
            assertThat(result.totalElements()).isOne();
            assertThat(result.totalPages()).isOne();
            assertThat(result.content()).isNotNull().hasSize(1);
            assertThat(result).isEqualTo(outputPage);
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
            given(orderUsecaseFacade.createOrder(input)).willReturn(order);

            // When
            OrderOutput result = orderController.create(input);

            // Then
            then(orderUsecaseFacade).should().createOrder(input);
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
            then(orderUsecaseFacade).shouldHaveNoInteractions();
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
            then(orderUsecaseFacade).should().confirmOrder(orderId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao confirmar com ID nulo")
        void deveLancarExcecaoAoConfirmarComIdNulo() {
            // When & Then
            assertThatThrownBy(() -> orderController.confirm(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage(ORDER_ID_CANNOT_BE_NULL_MESSAGE);
            then(orderUsecaseFacade).shouldHaveNoInteractions();
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
            then(orderUsecaseFacade).should().payOrder(orderId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao pagar com ID nulo")
        void deveLancarExcecaoAoPagarComIdNulo() {
            // When & Then
            assertThatThrownBy(() -> orderController.payOrder(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage(ORDER_ID_CANNOT_BE_NULL_MESSAGE);
            then(orderUsecaseFacade).shouldHaveNoInteractions();
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
            then(orderUsecaseFacade).should().pendingOrder(orderId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao marcar como pendente com ID nulo")
        void deveLancarExcecaoAoMarcarPendenteComIdNulo() {
            // When & Then
            assertThatThrownBy(() -> orderController.pendingPaymentOrder(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage(ORDER_ID_CANNOT_BE_NULL_MESSAGE);
            then(orderUsecaseFacade).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Validações do Construtor")
    class ConstructorValidation {

        @Test
        @DisplayName("Deve lançar exceção se orderUsecaseFacade for nulo")
        void deveLancarExcecaoSeOrderUsecaseFacadeForNulo() {
            assertThatThrownBy(() -> new OrderController(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("orderUsecaseFacade cannot be null.");
        }
    }
}
