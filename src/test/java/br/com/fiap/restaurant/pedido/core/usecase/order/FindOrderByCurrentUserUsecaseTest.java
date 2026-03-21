package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.domain.pagination.Page;
import br.com.fiap.restaurant.pedido.core.exception.UserNotAuthenticatedException;
import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para a busca de pedidos por usuário logado")
class FindOrderByCurrentUserUsecaseTest {

    private FindOrderByCurrentUserUsecase findOrderByCurrentUserUsecase;
    @Mock
    private LoggedUserGateway loggedUserGateway;
    @Mock
    private OrderGateway orderGateway;

    @BeforeEach
    void setup() {
        findOrderByCurrentUserUsecase = new FindOrderByCurrentUserUsecase(loggedUserGateway, orderGateway);
    }

    @Nested
    @DisplayName("Dado que um usuário logado busca por seus pedidos")
    class FindOrdersByLoggedInUser {

        private final UUID userUuid = UUID.randomUUID();
        private final Order order = new Order(1L, 1L, userUuid, new ArrayList<>(), LocalDateTime.now(), StatusOrder.DRAFT);
        private final int pageSize = 10;
        private final int pageNumber = 0;
        private final Set<StatusOrder> orderStatus = Set.of(StatusOrder.DRAFT);

        @Test
        @DisplayName("Deve retornar a lista de pedidos do usuário")
        void deveRetornarAListaDePedidosDoUsuario() {
            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.of(userUuid));
            when(orderGateway.findByUserAndStatus(userUuid, orderStatus, pageNumber, pageSize)).thenReturn(new Page<>(pageNumber, pageSize, 1, List.of(order)));

            Page<Order> result = findOrderByCurrentUserUsecase.findOrderByCurrentUser(orderStatus, pageNumber, pageSize);

            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(pageNumber);
            assertThat(result.pageSize()).isEqualTo(pageSize);
            assertThat(result.totalElements()).isOne();
            assertThat(result.content())
                    .isNotNull()
                    .hasSize(1)
                    .containsExactly(order);

            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).should().findByUserAndStatus(userUuid, orderStatus, pageNumber, pageSize);
            then(loggedUserGateway).shouldHaveNoMoreInteractions();
            then(orderGateway).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia se o usuário não tiver pedidos")
        void deveRetornarUmaListaVaziaSeOUsuarioNaoTiverPedidos() {
            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.of(userUuid));
            when(orderGateway.findByUserAndStatus(userUuid, orderStatus, pageNumber, pageSize)).thenReturn(new Page<>(pageNumber, pageSize, 0, List.of()));

            Page<Order> result = findOrderByCurrentUserUsecase.findOrderByCurrentUser(orderStatus, pageNumber, pageSize);

            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(pageNumber);
            assertThat(result.pageSize()).isEqualTo(pageSize);
            assertThat(result.totalElements()).isZero();
            assertThat(result.content()).isNotNull().isEmpty();

            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).should().findByUserAndStatus(userUuid, orderStatus, pageNumber, pageSize);
            then(loggedUserGateway).shouldHaveNoMoreInteractions();
            then(orderGateway).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve retornar uma com todos pedidos se o conjunto de status for vazia")
        void deveRetornarUmaComTodosPedidosSeOConjuntoDeStatusForVazio() {
            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.of(userUuid));
            when(orderGateway.findByUser(userUuid, pageNumber, pageSize)).thenReturn(new Page<>(pageNumber, pageSize, 1, List.of(order)));

            Page<Order> result = findOrderByCurrentUserUsecase.findOrderByCurrentUser(Set.of(), pageNumber, pageSize);

            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(pageNumber);
            assertThat(result.pageSize()).isEqualTo(pageSize);
            assertThat(result.totalElements()).isOne();
            assertThat(result.content())
                    .isNotNull()
                    .hasSize(1)
                    .containsExactly(order);

            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).should().findByUser(userUuid, pageNumber, pageSize);
            then(loggedUserGateway).shouldHaveNoMoreInteractions();
            then(orderGateway).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve retornar uma com todos pedidos se o conjunto de status for nula")
        void deveRetornarUmaComTodosPedidosSeOConjuntoDeStatusForNulo() {
            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.of(userUuid));
            when(orderGateway.findByUser(userUuid, pageNumber, pageSize)).thenReturn(new Page<>(pageNumber, pageSize, 1, List.of(order)));

            Page<Order> result = findOrderByCurrentUserUsecase.findOrderByCurrentUser(null, pageNumber, pageSize);

            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(pageNumber);
            assertThat(result.pageSize()).isEqualTo(pageSize);
            assertThat(result.totalElements()).isOne();
            assertThat(result.content())
                    .isNotNull()
                    .hasSize(1)
                    .containsExactly(order);

            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).should().findByUser(userUuid, pageNumber, pageSize);
            then(loggedUserGateway).shouldHaveNoMoreInteractions();
            then(orderGateway).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve tratar pageNumber e pagaSize inválido")
        void deveTratarPageNumberEPagaSizeInvalido() {
            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.of(userUuid));
            when(orderGateway.findByUser(userUuid, FindOrderByCurrentUserUsecase.DEFAULT_PAGE_NUMBER, FindOrderByCurrentUserUsecase.DEFAULT_PAGE_SIZE))
                    .thenReturn(new Page<>(FindOrderByCurrentUserUsecase.DEFAULT_PAGE_NUMBER, FindOrderByCurrentUserUsecase.DEFAULT_PAGE_SIZE, 1, List.of(order)));

            Page<Order> result = findOrderByCurrentUserUsecase.findOrderByCurrentUser(null, -1, 0);

            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(FindOrderByCurrentUserUsecase.DEFAULT_PAGE_NUMBER);
            assertThat(result.pageSize()).isEqualTo(FindOrderByCurrentUserUsecase.DEFAULT_PAGE_SIZE);
            assertThat(result.totalElements()).isOne();
            assertThat(result.content())
                    .isNotNull()
                    .hasSize(1)
                    .containsExactly(order);

            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).should().findByUser(userUuid, pageNumber, pageSize);
            then(loggedUserGateway).shouldHaveNoMoreInteractions();
            then(orderGateway).shouldHaveNoMoreInteractions();
        }

    }

    @Nested
    @DisplayName("Dado que um usuário não logado busca por pedidos")
    class FindOrdersByNotLoggedInUser {

        @Test
        @DisplayName("Deve lançar exceção de usuário não autenticado")
        void deveLancarExcecaoDeUsuarioNaoAutenticado() {
            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.empty());

            Set<StatusOrder> orderStatus = Set.of(StatusOrder.DRAFT);
            assertThatThrownBy(() -> findOrderByCurrentUserUsecase.findOrderByCurrentUser(orderStatus, 0, 10))
                    .isInstanceOf(UserNotAuthenticatedException.class)
                    .hasMessageContaining("User is not authenticated");

            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).shouldHaveNoInteractions();
            then(loggedUserGateway).shouldHaveNoMoreInteractions();
        }
    }

}
