package br.com.fiap.restaurant.pedido.infra.message.consumer;

import br.com.fiap.restaurant.pedido.core.controller.OrderController;
import br.com.fiap.restaurant.pedido.infra.config.RabbitMQConfig;
import br.com.fiap.restaurant.pedido.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.pedido.infra.message.dto.PaymentEventDTO;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    private final OrderController orderController;

    public OrderConsumer(OrderController orderController) {
        this.orderController = Objects.requireNonNull(orderController, "orderController cannot be null.");
    }

    @RabbitListener(queues = {RabbitMQConfig.ORDER_PAYMENT_APPROVED})
    public void approvePaymentOrder(EventDTO<PaymentEventDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            logger.info("Consuming approve payment event: {}", eventDTO);
            orderController.payOrder(eventDTO.body().orderId());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("Error consuming approve payment event: {}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }

    @RabbitListener(queues = {RabbitMQConfig.ORDER_PAYMENT_PENDING})
    public void pendingPaymentOrder(EventDTO<PaymentEventDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            logger.info("Consuming pending payment event: {}", eventDTO);
            orderController.pendingPaymentOrder(eventDTO.body().orderId());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("Error consuming pending payment event: {}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }
}
