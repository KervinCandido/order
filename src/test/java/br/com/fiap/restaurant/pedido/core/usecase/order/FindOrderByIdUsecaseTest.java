package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para a busca de pedido por Id")
class FindOrderByIdUsecaseTest {

    private FindOrderByIdUsecase findOrderByIdUsecase;
    @Mock
    private LoggedUserGateway loggedUserGateway;
    @Mock
    private OrderGateway orderGateway;


    @BeforeEach
    void setup() {
        findOrderByIdUsecase = new FindOrderByIdUsecase(loggedUserGateway, orderGateway);
    }

    @Nested
    @DisplayName("Dado que um usuário logado busca por uma ordem")
    class FindOrder {

        private final Long orderId = 1L;
        private final UUID userUuid = UUID.randomUUID();
        private final Order order = new Order(1L, 1L, userUuid, new ArrayList<>(), LocalDateTime.now(), StatusOrder.DRAFT);

        @Test
        @DisplayName("Deve retornar a ordem se o usuário for o dono, mesmo sem a permissão de visualização")
        void deveRetornarAOrdemSeOUsuarioForODonoMesmoSemAPermissaoDeVisualizacao() {

            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.of(userUuid));
            when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));

            var result = findOrderByIdUsecase.findById(orderId);

            assertThat(result).isPresent().contains(order);

            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).should().findById(orderId);
            then(loggedUserGateway).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve retornar a ordem se o usuário tiver permissão de visualização")
        void deveRetornarAOrdemSeOUsuarioTiverPermissaoDeVisualizacao() {
            var anotherUserUuid = UUID.randomUUID();
            var orderFromAnotherUser = new Order(1L, 1L, anotherUserUuid, new ArrayList<>(), LocalDateTime.now(), StatusOrder.CREATED);
            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.of(userUuid));
            when(loggedUserGateway.hasRole(Order.VIEW_ORDER)).thenReturn(true);
            when(orderGateway.findById(orderId)).thenReturn(Optional.of(orderFromAnotherUser));

            var result = findOrderByIdUsecase.findById(orderId);

            assertThat(result).isPresent().contains(orderFromAnotherUser);

            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).should().findById(orderId);
            then(loggedUserGateway).should().hasRole(Order.VIEW_ORDER);
        }

        @Test
        @DisplayName("Deve retornar vazio se o usuário não for o dono e não tiver permissão")
        void deveRetornarVazioSeOUsuarioNaoForODonoENaoTiverPermissao() {
            var anotherUserUuid = UUID.randomUUID();
            var orderFromAnotherUser = new Order(1L, 1L, anotherUserUuid, new ArrayList<>(), LocalDateTime.now(), StatusOrder.CREATED);
            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.of(userUuid));
            when(loggedUserGateway.hasRole(Order.VIEW_ORDER)).thenReturn(false);
            when(orderGateway.findById(orderId)).thenReturn(Optional.of(orderFromAnotherUser));

            var result = findOrderByIdUsecase.findById(orderId);

            assertThat(result).isNotPresent();

            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).should().findById(orderId);
            then(loggedUserGateway).should().hasRole(Order.VIEW_ORDER);
        }
    }

    @Nested
    @DisplayName("Dado que a ordem não existe")
    class OrderNotFound {

        @Test
        @DisplayName("Deve retornar vazio se a ordem não for encontrada")
        void deveRetornarVazioSeAOrdemNaoForEncontrada() {
            Long orderId = 1L;

            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.of(UUID.randomUUID()));
            when(orderGateway.findById(orderId)).thenReturn(Optional.empty());
            var result = findOrderByIdUsecase.findById(orderId);

            assertThat(result).isNotPresent();
            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).should().findById(orderId);
            then(loggedUserGateway).shouldHaveNoMoreInteractions();
        }
    }


    @Nested
    @DisplayName("Dado que um usuário não logado busca por uma ordem")
    class FindOrderWithoutLoggedUser {

        private final Long orderId = 1L;

        @Test
        @DisplayName("Deve lançar exceção de usuário não autenticado")
        void deveLancarExcecaoDeUsuarioNaoAutenticado() {
            when(loggedUserGateway.getCurrentUser()).thenReturn(Optional.empty());

            assertThatThrownBy(() -> findOrderByIdUsecase.findById(orderId))
                    .isInstanceOf(UserNotAuthenticatedException.class)
                    .hasMessageContaining("User is not authenticated");

            then(loggedUserGateway).should().getCurrentUser();
            then(orderGateway).shouldHaveNoInteractions();
            then(loggedUserGateway).shouldHaveNoMoreInteractions();
        }
    }

    @Test
    @DisplayName("Deve lançar exceção se o ID da ordem for nulo")
    void deveLancarExcecaoSeOIdDaOrdemForNulo() {
        assertThatThrownBy(() -> findOrderByIdUsecase.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("orderId cannot be null");

        then(orderGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).shouldHaveNoInteractions();
    }
}
