package br.com.fiap.restaurant.pedido.infra.message.consumer;

import br.com.fiap.restaurant.pedido.core.controller.MenuItemController;
import br.com.fiap.restaurant.pedido.core.inbound.MenuItemInput;
import br.com.fiap.restaurant.pedido.core.inbound.UpdateAllMenuItemsInput;
import br.com.fiap.restaurant.pedido.infra.config.RabbitMQConfig;
import br.com.fiap.restaurant.pedido.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.pedido.infra.message.dto.RestaurantDTO;
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
public class CreateRestaurantConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CreateRestaurantConsumer.class);
    private final MenuItemController menuItemController;

    public CreateRestaurantConsumer(MenuItemController menuItemController) {
        this.menuItemController = Objects.requireNonNull(menuItemController, "menuItemController cannot be null.");
    }

    @RabbitListener(queues = {RabbitMQConfig.RESTAURANT_CREATE_QUEUE})
    public void createRestaurant(EventDTO<RestaurantDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            logger.info("Consuming create restaurant event: {}", eventDTO);
            var restaurantDTO = eventDTO.body();
            var updateAllMenuItemsInput = new UpdateAllMenuItemsInput(restaurantDTO.id(), restaurantDTO
                    .menu()
                    .parallelStream()
                    .map(i -> new MenuItemInput(i.id(), i.name(), i.price(), i.restaurantOnly()))
                    .toList());
            menuItemController.updateAllMenuItems(updateAllMenuItemsInput);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("Error consuming create restaurant event: {}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }
}
