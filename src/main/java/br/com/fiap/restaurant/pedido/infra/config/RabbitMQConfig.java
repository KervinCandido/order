package br.com.fiap.restaurant.pedido.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "ex.order";
    public static final String CREATED_ORDER_QUEUE = "payment.order.created";

    public static final String CONFIRM_ORDER_ROUTING_KEY = "order.created";

    /*Consumer*/
    public static final String RESTAURANT_CREATE_QUEUE = "order.restaurant.created";
    public static final String RESTAURANT_UPDATE_QUEUE = "order.restaurant.updated";
    public static final String RESTAURANT_DELETE_QUEUE = "order.restaurant.deleted";

    public static final String MENU_ITEM_CREATE_QUEUE = "order.menuitem.created";
    public static final String MENU_ITEM_UPDATE_QUEUE = "order.menuitem.updated";
    public static final String MENU_ITEM_DELETE_QUEUE = "order.menuitem.deleted";

    public static final String ORDER_PAYMENT_APPROVED = "order.payment.approved";
    public static final String ORDER_PAYMENT_PENDING = "order.payment.pending";

    @Bean("orderExchange")
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean("createdOrderQueue")
    public Queue createdOrderQueue() {
        return QueueBuilder.durable(CREATED_ORDER_QUEUE).quorum().build();
    }

    @Bean
    public Binding binding(@Qualifier("createdOrderQueue") Queue queue, @Qualifier("orderExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(CONFIRM_ORDER_ROUTING_KEY);
    }

    /*Consumer*/

    @Bean("createRestaurantQueue")
    public Queue createRestaurantQueue() {
        return QueueBuilder
                .durable(RESTAURANT_CREATE_QUEUE)
                .quorum()
                .build();
    }

    @Bean("updateRestaurantQueue")
    public Queue updateRestaurantQueue() {
        return QueueBuilder
                .durable(RESTAURANT_UPDATE_QUEUE)
                .quorum()
                .build();
    }

    @Bean("deleteRestaurantQueue")
    public Queue deleteRestaurantQueue() {
        return QueueBuilder
                .durable(RESTAURANT_DELETE_QUEUE)
                .quorum()
                .build();
    }

    @Bean("menuItemCreateQueue")
    public Queue menuItemCreateQueue() {
        return QueueBuilder
                .durable(MENU_ITEM_CREATE_QUEUE)
                .quorum()
                .build();
    }

    @Bean("menuItemUpdateQueue")
    public Queue menuItemUpdateQueue() {
        return QueueBuilder
                .durable(MENU_ITEM_UPDATE_QUEUE)
                .quorum()
                .build();
    }

    @Bean("menuItemDeleteQueue")
    public Queue menuItemDeleteQueue() {
        return QueueBuilder
                .durable(MENU_ITEM_DELETE_QUEUE)
                .quorum()
                .build();
    }

    @Bean("orderPaymentApproved")
    public Queue orderPaymentApproved() {
        return QueueBuilder
                .durable(ORDER_PAYMENT_APPROVED)
                .quorum()
                .build();
    }

    @Bean("orderPaymentPending")
    public Queue orderPaymentPending() {
        return QueueBuilder
                .durable(ORDER_PAYMENT_PENDING)
                .quorum()
                .build();
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
