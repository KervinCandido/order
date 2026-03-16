package br.com.fiap.restaurant.pedido.infra.message.consumer;

import br.com.fiap.restaurant.pedido.core.controller.MenuItemController;
import br.com.fiap.restaurant.pedido.core.inbound.MenuItemInput;
import br.com.fiap.restaurant.pedido.infra.config.RabbitMQConfig;
import br.com.fiap.restaurant.pedido.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.pedido.infra.message.dto.MenuItemDTO;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class MenuItemConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemConsumer.class);

    private final MenuItemController menuItemController;

    public MenuItemConsumer(MenuItemController menuItemController) {
        this.menuItemController = menuItemController;
    }

    @RabbitListener(queues = {RabbitMQConfig.MENU_ITEM_CREATE_QUEUE})
    public void createMenuItem(EventDTO<MenuItemDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            logger.info("Consuming create menu item event: {}", eventDTO);
            var menuItemDTO = eventDTO.body();
            menuItemController.createMenuItems(List.of(toMenuInput(menuItemDTO)));
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("Error consuming create menu item event: {}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }

    @RabbitListener(queues = {RabbitMQConfig.MENU_ITEM_UPDATE_QUEUE})
    public void updateMenuItem(EventDTO<MenuItemDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            logger.info("Consuming update menu item event: {}", eventDTO);
            var menuItemDTO = eventDTO.body();
            menuItemController.updateMenuItem(toMenuInput(menuItemDTO));
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("Error consuming update menu item event: {}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }
    @RabbitListener(queues = {RabbitMQConfig.MENU_ITEM_DELETE_QUEUE})
    public void deleteMenuItem(EventDTO<MenuItemDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            logger.info("Consuming delete menu item event: {}", eventDTO);
            var menuItemDTO = eventDTO.body();
            menuItemController.deleteMenuItem(menuItemDTO.id());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("Error consuming delete menu item event: {}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }

    private MenuItemInput toMenuInput(MenuItemDTO menuItemDTO) {
        return new MenuItemInput(menuItemDTO.id(), menuItemDTO.name(), menuItemDTO.price(),
                menuItemDTO.restaurantOnly(), menuItemDTO.restaurantId());
    }
}
