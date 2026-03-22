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

    public static final String ORDER_EXCHANGE_NAME = "ex.order";
    public static final String ORDER_DLX_EXCHANGE_NAME = "ex.order.dlx";
    public static final String PAYMENT_DLX_EXCHANGE_NAME = "ex.payment.dlx";
    public static final String CREATED_ORDER_QUEUE = "payment.order.created";
    public static final String CREATED_ORDER_QUEUE_DLQ = CREATED_ORDER_QUEUE + ".dlq";

    public static final String CONFIRM_ORDER_ROUTING_KEY = "order.created";

    /*Consumer*/
    public static final String RESTAURANT_CREATE_QUEUE = "order.restaurant.created";
    public static final String RESTAURANT_UPDATE_QUEUE = "order.restaurant.updated";
    public static final String RESTAURANT_DELETE_QUEUE = "order.restaurant.deleted";

    public static final String MENU_ITEM_CREATE_QUEUE = "order.menuitem.created";
    public static final String MENU_ITEM_UPDATE_QUEUE = "order.menuitem.updated";
    public static final String MENU_ITEM_DELETE_QUEUE = "order.menuitem.deleted";

    public static final String ORDER_PAYMENT_APPROVED = "order.payment.approved";
    public static final String ORDER_PAYMENT_APPROVED_DLQ = ORDER_PAYMENT_APPROVED + ".dlq";
    public static final String ORDER_PAYMENT_PENDING = "order.payment.pending";
    public static final String ORDER_PAYMENT_PENDING_DLQ = ORDER_PAYMENT_PENDING + ".dlq";

    @Bean("orderExchange")
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE_NAME);
    }

    @Bean("orderDlxExchange")
    public DirectExchange orderDlxExchange() {
        return new DirectExchange(ORDER_DLX_EXCHANGE_NAME);
    }

    @Bean("createdOrderQueue")
    public Queue createdOrderQueue() {
        return QueueBuilder.durable(CREATED_ORDER_QUEUE)
                .quorum()
                .deadLetterExchange(ORDER_DLX_EXCHANGE_NAME)
                .deadLetterRoutingKey(CREATED_ORDER_QUEUE)
                .build();
    }

    @Bean("createdOrderDlq")
    public Queue createdOrderDlq() {
        return QueueBuilder.durable(CREATED_ORDER_QUEUE_DLQ).quorum().build();
    }

    @Bean
    public Binding dlqBindingCreatedOrder(@Qualifier("createdOrderDlq") Queue queue, @Qualifier("orderDlxExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(CREATED_ORDER_QUEUE);
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
                .deadLetterExchange(PAYMENT_DLX_EXCHANGE_NAME)
                .deadLetterRoutingKey(ORDER_PAYMENT_APPROVED)
                .build();
    }

    @Bean("orderPaymentApprovedDlq")
    public Queue orderPaymentApprovedDlq() {
        return QueueBuilder.durable(ORDER_PAYMENT_APPROVED_DLQ).quorum().build();
    }

    @Bean
    public Binding dlqBindingOrderPaymentApproved(@Qualifier("orderPaymentApprovedDlq") Queue queue, @Qualifier("orderDlxExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(ORDER_PAYMENT_APPROVED);
    }

    @Bean("orderPaymentPending")
    public Queue orderPaymentPending() {
        return QueueBuilder
                .durable(ORDER_PAYMENT_PENDING)
                .quorum()
                .deadLetterExchange(PAYMENT_DLX_EXCHANGE_NAME)
                .deadLetterRoutingKey(ORDER_PAYMENT_PENDING)
                .build();
    }

    @Bean("orderPaymentPendingDlq")
    public Queue orderPaymentPendingDlq() {
        return QueueBuilder.durable(ORDER_PAYMENT_PENDING_DLQ).quorum().build();
    }

    @Bean
    public Binding dlqBindingOrderPaymentPending(@Qualifier("orderPaymentPendingDlq") Queue queue, @Qualifier("orderDlxExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(ORDER_PAYMENT_PENDING);
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
