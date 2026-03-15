package br.com.fiap.restaurant.pedido.infra.message.publisher;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.pedido.infra.config.RabbitMQConfig;
import br.com.fiap.restaurant.pedido.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.pedido.infra.message.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ConfirmOrderPublisher implements PublisherGateway <Order> {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmOrderPublisher.class);
    public static final String CONFIRM_ORDER_EVENT_TYPE = "order.confirm";

    private final RabbitTemplate rabbitTemplate;

    public ConfirmOrderPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public CompletableFuture<Void> publish(Order event) {
        OrderDTO orderDTO = new OrderDTO(event);
        EventDTO<OrderDTO> eventDTO = new EventDTO<>(CONFIRM_ORDER_EVENT_TYPE, orderDTO);
        logger.info("Publishing confirm order event: {}", eventDTO);
        return CompletableFuture.runAsync(() -> rabbitTemplate
                .convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.CONFIRM_ORDER_ROUTING_KEY, eventDTO));
    }
}
