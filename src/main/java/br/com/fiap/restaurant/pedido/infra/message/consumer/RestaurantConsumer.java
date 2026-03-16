package br.com.fiap.restaurant.pedido.infra.message.consumer;

import br.com.fiap.restaurant.pedido.core.controller.MenuItemController;
import br.com.fiap.restaurant.pedido.core.inbound.MenuItemInput;
import br.com.fiap.restaurant.pedido.infra.config.RabbitMQConfig;
import br.com.fiap.restaurant.pedido.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.pedido.infra.message.dto.MenuItemDTO;
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
import java.util.function.Function;

@Component
public class RestaurantConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantConsumer.class);
    private final MenuItemController menuItemController;

    public RestaurantConsumer(MenuItemController menuItemController) {
        this.menuItemController = Objects.requireNonNull(menuItemController, "menuItemController cannot be null.");
    }

    @RabbitListener(queues = {RabbitMQConfig.RESTAURANT_CREATE_QUEUE})
    public void createRestaurant(EventDTO<RestaurantDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            logger.info("Consuming create restaurant event: {}", eventDTO);
            var restaurantDTO = eventDTO.body();
            var menuItemsInput = restaurantDTO.menu().parallelStream().map(toMenuItemInput()).toList();
            menuItemController.createMenuItems(menuItemsInput);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("Error consuming create restaurant event: {}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }

    @RabbitListener(queues = {RabbitMQConfig.RESTAURANT_UPDATE_QUEUE})
    public void updateRestaurant(EventDTO<RestaurantDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            logger.info("Consuming update restaurant event: {}", eventDTO);
            var restaurantDTO = eventDTO.body();
            var updateAllMenuItemsInput = restaurantDTO.menu().parallelStream().map(toMenuItemInput()).toList();
            menuItemController.updateAllMenuItemsOfRestaurant(restaurantDTO.id(), updateAllMenuItemsInput);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("Error update create restaurant event: {}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }

    @RabbitListener(queues = {RabbitMQConfig.RESTAURANT_DELETE_QUEUE})
    public void deleteRestaurant(EventDTO<RestaurantDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            logger.info("Consuming delete restaurant event: {}", eventDTO);
            var restaurantDTO = eventDTO.body();
            menuItemController.deleteByRestaurantId(restaurantDTO.id());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("Error delete create restaurant event: {}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }

    private Function<MenuItemDTO, MenuItemInput> toMenuItemInput() {
        return i -> new MenuItemInput(i.id(), i.name(), i.price(), i.restaurantOnly(), i.restaurantId());
    }
}
