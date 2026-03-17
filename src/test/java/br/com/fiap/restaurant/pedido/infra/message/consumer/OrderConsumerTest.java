package br.com.fiap.restaurant.pedido.infra.message.consumer;

import br.com.fiap.restaurant.pedido.core.controller.OrderController;
import br.com.fiap.restaurant.pedido.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.pedido.infra.message.dto.PaymentEventDTO;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o consumidor OrderConsumer")
class OrderConsumerTest {

    @Mock
    private OrderController orderController;

    @Mock
    private Channel channel;

    private OrderConsumer orderConsumer;

    private static final long DELIVERY_TAG = 1L;

    @BeforeEach
    void setUp() {
        orderConsumer = new OrderConsumer(orderController);
    }

    private EventDTO<PaymentEventDTO> createTestEvent(Long orderId) {
        var paymentEventDTO = new PaymentEventDTO(UUID.randomUUID(), orderId, UUID.randomUUID(), new BigDecimal("150"), "APPROVED");
        return new EventDTO<>("payment-event", paymentEventDTO);
    }

    @Nested
    @DisplayName("Consumo de evento de pagamento aprovado")
    class ApprovePayment {

        @Test
        @DisplayName("Deve processar evento de pagamento aprovado e confirmar a mensagem")
        void deveProcessarPagamentoAprovadoEConfirmarMensagem() throws IOException {
            // Given
            var orderId = 100L;
            var event = createTestEvent(orderId);
            doNothing().when(orderController).payOrder(orderId);

            // When
            orderConsumer.approvePaymentOrder(event, channel, DELIVERY_TAG);

            // Then
            then(orderController).should().payOrder(orderId);
            then(channel).should().basicAck(DELIVERY_TAG, false);
            then(channel).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve rejeitar a mensagem em caso de erro no processamento de pagamento aprovado")
        void deveRejeitarMensagemEmCasoDeErroNoPagamentoAprovado() throws IOException {
            // Given
            var orderId = 100L;
            var event = createTestEvent(orderId);
            willThrow(new RuntimeException("Test error")).given(orderController).payOrder(orderId);

            // When
            orderConsumer.approvePaymentOrder(event, channel, DELIVERY_TAG);

            // Then
            then(orderController).should().payOrder(orderId);
            then(channel).should().basicNack(DELIVERY_TAG, false, true);
            then(channel).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("Consumo de evento de pagamento pendente")
    class PendingPayment {

        @Test
        @DisplayName("Deve processar evento de pagamento pendente e confirmar a mensagem")
        void deveProcessarPagamentoPendenteEConfirmarMensagem() throws IOException {
            // Given
            var orderId = 200L;
            var event = createTestEvent(orderId);
            doNothing().when(orderController).pendingPaymentOrder(orderId);

            // When
            orderConsumer.pendingPaymentOrder(event, channel, DELIVERY_TAG);

            // Then
            then(orderController).should().pendingPaymentOrder(orderId);
            then(channel).should().basicAck(DELIVERY_TAG, false);
            then(channel).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("Deve rejeitar a mensagem em caso de erro no processamento de pagamento pendente")
        void deveRejeitarMensagemEmCasoDeErroNoPagamentoPendente() throws IOException {
            // Given
            var orderId = 200L;
            var event = createTestEvent(orderId);
            willThrow(new RuntimeException("Test error")).given(orderController).pendingPaymentOrder(orderId);

            // When
            orderConsumer.pendingPaymentOrder(event, channel, DELIVERY_TAG);

            // Then
            then(orderController).should().pendingPaymentOrder(orderId);
            then(channel).should().basicNack(DELIVERY_TAG, false, true);
            then(channel).shouldHaveNoMoreInteractions();
        }
    }
}
