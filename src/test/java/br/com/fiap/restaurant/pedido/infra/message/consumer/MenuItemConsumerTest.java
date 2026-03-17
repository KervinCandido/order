package br.com.fiap.restaurant.pedido.infra.message.consumer;

import br.com.fiap.restaurant.pedido.core.controller.MenuItemController;
import br.com.fiap.restaurant.pedido.core.inbound.MenuItemInput;
import br.com.fiap.restaurant.pedido.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.pedido.infra.message.dto.MenuItemDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o consumidor MenuItemConsumer")
class MenuItemConsumerTest {

    @Mock
    private MenuItemController menuItemController;

    @Mock
    private Channel channel;

    private MenuItemConsumer menuItemConsumer;

    private static final long DELIVERY_TAG = 1L;

    @Captor
    private ArgumentCaptor<List<MenuItemInput>> menuItemInputListCaptor;
    @Captor
    private ArgumentCaptor<MenuItemInput> menuItemInputCaptor;

    @BeforeEach
    void setUp() {
        menuItemConsumer = new MenuItemConsumer(menuItemController);
    }

    private EventDTO<MenuItemDTO> createTestEvent() {
        var menuItemDTO = new MenuItemDTO(1L, "Test Item", BigDecimal.TEN, false, 1L);
        return new EventDTO<>("test-event", menuItemDTO);
    }

    @Nested
    @DisplayName("Consumo de evento de criação de item de menu")
    class CreateMenuItem {

        @Test
        @DisplayName("Deve processar evento de criação e confirmar a mensagem")
        void deveProcessarEventoDeCriacaoEConfirmarMensagem() throws IOException {
            // Given
            var event = createTestEvent();

            // When
            menuItemConsumer.createMenuItem(event, channel, DELIVERY_TAG);

            // Then
            then(menuItemController).should().createMenuItems(menuItemInputListCaptor.capture());
            List<MenuItemInput> capturedList = menuItemInputListCaptor.getValue();
            assertThat(capturedList).hasSize(1);
            assertThat(capturedList.getFirst().id()).isEqualTo(event.body().id());

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
            menuItemConsumer.createMenuItem(event, channel, DELIVERY_TAG);

            // Then
            then(channel).should().basicNack(DELIVERY_TAG, false, true);
            then(channel).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("Consumo de evento de atualização de item de menu")
    class UpdateMenuItem {

        @Test
        @DisplayName("Deve processar evento de atualização e confirmar a mensagem")
        void deveProcessarEventoDeAtualizacaoEConfirmarMensagem() throws IOException {
            // Given
            var event = createTestEvent();

            // When
            menuItemConsumer.updateMenuItem(event, channel, DELIVERY_TAG);

            // Then
            then(menuItemController).should().updateMenuItem(menuItemInputCaptor.capture());
            MenuItemInput capturedInput = menuItemInputCaptor.getValue();
            assertThat(capturedInput.id()).isEqualTo(event.body().id());

            then(channel).should().basicAck(DELIVERY_TAG, false);
            then(channel).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve rejeitar a mensagem em caso de erro no processamento de atualização")
        void deveRejeitarMensagemEmCasoDeErroNaAtualizacao() throws IOException {
            // Given
            var event = createTestEvent();
            willThrow(new RuntimeException("Test error")).given(menuItemController).updateMenuItem(any());

            // When
            menuItemConsumer.updateMenuItem(event, channel, DELIVERY_TAG);

            // Then
            then(channel).should().basicNack(DELIVERY_TAG, false, true);
            then(channel).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("Consumo de evento de exclusão de item de menu")
    class DeleteMenuItem {

        @Test
        @DisplayName("Deve processar evento de exclusão e confirmar a mensagem")
        void deveProcessarEventoDeExclusaoEConfirmarMensagem() throws IOException {
            // Given
            var event = createTestEvent();
            var menuItemId = event.body().id();

            // When
            menuItemConsumer.deleteMenuItem(event, channel, DELIVERY_TAG);

            // Then
            then(menuItemController).should().deleteMenuItem(menuItemId);
            then(channel).should().basicAck(DELIVERY_TAG, false);
            then(channel).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve rejeitar a mensagem em caso de erro no processamento de exclusão")
        void deveRejeitarMensagemEmCasoDeErroNaExclusao() throws IOException {
            // Given
            var event = createTestEvent();
            var menuItemId = event.body().id();
            willThrow(new RuntimeException("Test error")).given(menuItemController).deleteMenuItem(menuItemId);

            // When
            menuItemConsumer.deleteMenuItem(event, channel, DELIVERY_TAG);

            // Then
            then(channel).should().basicNack(DELIVERY_TAG, false, true);
            then(channel).shouldHaveNoMoreInteractions();
        }
    }
}
