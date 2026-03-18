package br.com.fiap.restaurant.pedido.infra.message.publisher;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.pedido.infra.message.dto.OrderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o publisher ConfirmOrderPublisher")
class ConfirmOrderPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private ConfirmOrderPublisher confirmOrderPublisher;

    @Captor
    private ArgumentCaptor<EventDTO<OrderDTO>> eventDTOCaptor;

    @BeforeEach
    void setUp() {
        confirmOrderPublisher = new ConfirmOrderPublisher(rabbitTemplate);
    }

    @Nested
    @DisplayName("Publicação de Evento de Confirmação de Pedido")
    class PublishEvent {

        @Test
        @DisplayName("Deve publicar um evento de confirmação de pedido com sucesso")
        void devePublicarEventoDeConfirmacaoComSucesso() throws Exception {
            // Given
            var order = new Order(1L, 1L, UUID.randomUUID(), new ArrayList<>(), LocalDateTime.now(), StatusOrder.APPROVED);

            // When
            CompletableFuture<Void> future = confirmOrderPublisher.publish(order);
            future.get(5, TimeUnit.SECONDS); // Aguarda a conclusão do CompletableFuture

            // Then
            then(rabbitTemplate).should().convertAndSend(
                    anyString(),
                    anyString(),
                    eventDTOCaptor.capture()
            );

            EventDTO<OrderDTO> capturedEvent = eventDTOCaptor.getValue();
            assertThat(capturedEvent.type()).isEqualTo(ConfirmOrderPublisher.CONFIRM_ORDER_EVENT_TYPE);
            assertThat(capturedEvent.body().id()).isEqualTo(order.getId());
            assertThat(capturedEvent.body().customerUuid()).isEqualTo(order.getCustomerUuid());
        }
    }

    @Nested
    @DisplayName("Validação do Construtor")
    class ConstructorValidation {

        @Test
        @DisplayName("Deve lançar NullPointerException se RabbitTemplate for nulo")
        void deveLancarExcecaoSeRabbitTemplateForNulo() {
            // When & Then
            assertThatThrownBy(() -> new ConfirmOrderPublisher(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("rabbitTemplate cannot be null");
        }
    }
}
