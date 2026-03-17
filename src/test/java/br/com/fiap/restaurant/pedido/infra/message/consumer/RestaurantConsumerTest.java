package br.com.fiap.restaurant.pedido.infra.message.consumer;

import br.com.fiap.restaurant.pedido.core.controller.MenuItemController;
import br.com.fiap.restaurant.pedido.core.inbound.MenuItemInput;
import br.com.fiap.restaurant.pedido.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.pedido.infra.message.dto.MenuItemDTO;
import br.com.fiap.restaurant.pedido.infra.message.dto.RestaurantDTO;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o consumidor RestaurantConsumer")
class RestaurantConsumerTest {

    @Mock
    private MenuItemController menuItemController;
    @Mock
    private Channel channel;

    private RestaurantConsumer restaurantConsumer;

    private static final long DELIVERY_TAG = 1L;

    @Captor
    private ArgumentCaptor<List<MenuItemInput>> menuItemsInputCaptor;

    @BeforeEach
    void setUp() {
        restaurantConsumer = new RestaurantConsumer(menuItemController);
    }

    private EventDTO<RestaurantDTO> createTestEvent() {
        var menuItemDto = new MenuItemDTO(1L, "Item", BigDecimal.TEN, false, 1L);
        var restaurantDto = new RestaurantDTO(1L, Set.of(menuItemDto));
        return new EventDTO<>("test-event", restaurantDto);
    }

    @Nested
    @DisplayName("Consumo de evento de criação de restaurante")
    class CreateRestaurant {

        @Test
        @DisplayName("Deve processar evento de criação e confirmar a mensagem")
        void deveProcessarEventoDeCriacaoEConfirmarMensagem() throws IOException {
            // Given
            var event = createTestEvent();

            // When
            restaurantConsumer.createRestaurant(event, channel, DELIVERY_TAG);

            // Then
            then(menuItemController).should().createMenuItems(menuItemsInputCaptor.capture());
            List<MenuItemInput> capturedList = menuItemsInputCaptor.getValue();
            assertThat(capturedList).hasSize(1)
                    .containsExactlyInAnyOrderElementsOf(event.body()
                    .menu().parallelStream()
                    .map(i -> new MenuItemInput(i.id(), i.name(), i.price(), i.restaurantOnly(), i.restaurantId())).toList());

            then(channel).should().basicAck(DELIVERY_TAG, false);
            then(channel).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve rejeitar a mensagem em caso de erro no processamento de criação")
        void deveRejeitarMensagemEmCasoDeErroNaCriacao() throws IOException {
            // Given
            var event = createTestEvent();
            willThrow(new RuntimeException("Test error")).given(menuItemController).createMenuItems(any());

            // When
            restaurantConsumer.createRestaurant(event, channel, DELIVERY_TAG);

            // Then
            then(channel).should().basicNack(DELIVERY_TAG, false, true);
            then(channel).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("Consumo de evento de atualização de restaurante")
    class UpdateRestaurant {

        @Test
        @DisplayName("Deve processar evento de atualização e confirmar a mensagem")
        void deveProcessarEventoDeAtualizacaoEConfirmarMensagem() throws IOException {
            // Given
            var event = createTestEvent();

            // When
            restaurantConsumer.updateRestaurant(event, channel, DELIVERY_TAG);

            // Then
            then(menuItemController).should().updateAllMenuItemsOfRestaurant(any(Long.class), menuItemsInputCaptor.capture());
            List<MenuItemInput> capturedList = menuItemsInputCaptor.getValue();
            assertThat(capturedList).hasSize(1)
                    .containsExactlyInAnyOrderElementsOf(event.body()
                    .menu().parallelStream()
                    .map(i -> new MenuItemInput(i.id(), i.name(), i.price(), i.restaurantOnly(), i.restaurantId())).toList());

            then(channel).should().basicAck(DELIVERY_TAG, false);
            then(channel).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve rejeitar a mensagem em caso de erro no processamento de atualização")
        void deveRejeitarMensagemEmCasoDeErroNaAtualizacao() throws IOException {
            // Given
            var event = createTestEvent();
            willThrow(new RuntimeException("Test error")).given(menuItemController).updateAllMenuItemsOfRestaurant(any(), any());

            // When
            restaurantConsumer.updateRestaurant(event, channel, DELIVERY_TAG);

            // Then
            then(channel).should().basicNack(DELIVERY_TAG, false, true);
            then(channel).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("Consumo de evento de exclusão de restaurante")
    class DeleteRestaurant {

        @Test
        @DisplayName("Deve processar evento de exclusão e confirmar a mensagem")
        void deveProcessarEventoDeExclusaoEConfirmarMensagem() throws IOException {
            // Given
            var event = createTestEvent();
            var restaurantId = event.body().id();

            // When
            restaurantConsumer.deleteRestaurant(event, channel, DELIVERY_TAG);

            // Then
            then(menuItemController).should().deleteByRestaurantId(restaurantId);
            then(channel).should().basicAck(DELIVERY_TAG, false);
            then(channel).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve rejeitar a mensagem em caso de erro no processamento de exclusão")
        void deveRejeitarMensagemEmCasoDeErroNaExclusao() throws IOException {
            // Given
            var event = createTestEvent();
            var restaurantId = event.body().id();
            willThrow(new RuntimeException("Test error")).given(menuItemController).deleteByRestaurantId(restaurantId);

            // When
            restaurantConsumer.deleteRestaurant(event, channel, DELIVERY_TAG);

            // Then
            then(channel).should().basicNack(DELIVERY_TAG, false, true);
            then(channel).shouldHaveNoMoreInteractions();
        }
    }
}
