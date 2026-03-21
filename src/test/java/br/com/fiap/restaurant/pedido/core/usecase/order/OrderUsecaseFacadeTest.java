package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.OrderItem;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.domain.pagination.Page;
import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Facade de Caso de Uso de Pedido")
class OrderUsecaseFacadeTest {

    @Mock
    private PendingOrderUseCase pendingOrderUseCase;
    @Mock
    private ConfirmOrderUseCase confirmOrderUseCase;
    @Mock
    private CreateOrderUsecase createOrderUsecase;
    @Mock
    private PayOrderUseCase payOrderUseCase;
    @Mock
    private FindOrderByCurrentUserUsecase findOrderByCurrentUserUsecase;
    @Mock
    private FindOrderByIdUsecase findOrderByIdUsecase;

    private OrderUsecaseFacade orderUsecaseFacade;

    @BeforeEach
    void setUp() {
        orderUsecaseFacade = new OrderUsecaseFacade.Builder()
                .pendingOrderUseCase(pendingOrderUseCase)
                .confirmOrderUseCase(confirmOrderUseCase)
                .createOrderUsecase(createOrderUsecase)
                .payOrderUseCase(payOrderUseCase)
                .findOrderByCurrentUserUsecase(findOrderByCurrentUserUsecase)
                .findOrderByIdUsecase(findOrderByIdUsecase)
                .build();
    }

    @Nested
    @DisplayName("Testes de pendência de pedido")
    class PendingOrderTest {
        @Test
        @DisplayName("Deve colocar o pedido como pendente")
        void deveColocarPedidoComoPendente() {
            // Dado (Given)
            var orderId = 1L;
            willDoNothing().given(pendingOrderUseCase).pendingOrderById(orderId);

            // Quando (When)
            orderUsecaseFacade.pendingOrder(orderId);

            // Então (Then)
            then(pendingOrderUseCase).should().pendingOrderById(orderId);
        }
    }

    @Nested
    @DisplayName("Testes de confirmação de pedido")
    class ConfirmOrderTest {
        @Test
        @DisplayName("Deve confirmar o pedido")
        void deveConfirmarPedido() {
            // Dado (Given)
            var orderId = 1L;
            willDoNothing().given(confirmOrderUseCase).confirmOrderBy(orderId);

            // Quando (When)
            orderUsecaseFacade.confirmOrder(orderId);

            // Então (Then)
            then(confirmOrderUseCase).should().confirmOrderBy(orderId);
        }
    }

    @Nested
    @DisplayName("Testes de criação de pedido")
    class CreateOrderTest {
        @Test
        @DisplayName("Deve criar um novo pedido")
        void deveCriarNovoPedido() {
            // Dado (Given)
            var input = mock(CreateOrderInput.class);
            var orderItems = List.of(
                new OrderItem(
                    new MenuItem(1L, "Hambúrguer", java.math.BigDecimal.TEN, false, 1L),
                    new BigDecimal("10.00")
                )
            );
            var expectedOrder = new Order(1L, 1L, UUID.randomUUID(), orderItems, LocalDateTime.now(), StatusOrder.DRAFT);
            given(createOrderUsecase.create(input)).willReturn(expectedOrder);

            // Quando (When)
            var result = orderUsecaseFacade.createOrder(input);

            // Então (Then)
            assertThat(result).isEqualTo(expectedOrder);
            then(createOrderUsecase).should().create(input);
        }
    }

    @Nested
    @DisplayName("Testes de pagamento de pedido")
    class PayOrderTest {
        @Test
        @DisplayName("Deve pagar o pedido")
        void devePagarPedido() {
            // Dado (Given)
            var orderId = 1L;
            willDoNothing().given(payOrderUseCase).payOrderById(orderId);

            // Quando (When)
            orderUsecaseFacade.payOrder(orderId);

            // Então (Then)
            then(payOrderUseCase).should().payOrderById(orderId);
        }
    }

    @Nested
    @DisplayName("Testes de busca de pedido")
    class FindOrderTest {

        private Order order;

        @BeforeEach
        void setUp() {
            var orderItems = List.of(
                    new OrderItem(
                            new MenuItem(1L, "Hambúrguer", java.math.BigDecimal.TEN, false, 1L),
                            new BigDecimal("10.00")
                    )
            );
            this.order = new Order(1L, 1L, UUID.randomUUID(), orderItems, LocalDateTime.now(), StatusOrder.DRAFT);
        }

        @Test
        @DisplayName("Deve encontrar o pedido por ID")
        void deveEncontrarPedidoPorId() {
            // Dado (Given)
            var orderId = order.getId();
            given(findOrderByIdUsecase.findById(orderId)).willReturn(Optional.of(order));

            // Quando (When)
            var result = orderUsecaseFacade.findOrderById(orderId);

            // Então (Then)
            assertThat(result).isPresent().contains(order);
            then(findOrderByIdUsecase).should().findById(orderId);
        }

        @Test
        @DisplayName("Deve encontrar pedidos por usuário atual e status")
        void deveEncontrarPedidosPorUsuarioEStatus() {
            // Dado (Given)
            var status = Collections.singleton(StatusOrder.DRAFT);
            var pageNumber = 0;
            var pageSize = 10;
            var expectedPage = new Page<>(pageNumber, pageNumber, 1, List.of(order));
            given(findOrderByCurrentUserUsecase.findOrderByCurrentUser(status, pageNumber, pageSize)).willReturn(expectedPage);

            // Quando (When)
            var result = orderUsecaseFacade.findOrderByCurrentUser(status, pageNumber, pageSize);

            // Então (Then)
            assertThat(result).isEqualTo(expectedPage);
            then(findOrderByCurrentUserUsecase).should().findOrderByCurrentUser(status, pageNumber, pageSize);
        }
    }
}
