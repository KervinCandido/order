package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.domain.pagination.Page;
import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import br.com.fiap.restaurant.pedido.core.outbound.OrderOutput;
import br.com.fiap.restaurant.pedido.core.presenter.OrderPresenter;
import br.com.fiap.restaurant.pedido.core.usecase.order.OrderUsecaseFacade;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class OrderController {

    public static final String ORDER_ID_CANNOT_BE_NULL_MESSAGE = "orderId cannot be null";

    private final OrderUsecaseFacade orderUsecaseFacade;

    public OrderController(OrderUsecaseFacade orderUsecaseFacade) {
        this.orderUsecaseFacade = Objects.requireNonNull(orderUsecaseFacade, "orderUsecaseFacade cannot be null.");
    }

    public OrderOutput create(CreateOrderInput input) {
        Objects.requireNonNull(input, "createOrderInput cannot be null.");
        var order = orderUsecaseFacade.createOrder(input);
        return OrderPresenter.toOutput(order);
    }

    public void confirm(Long orderId) {
        Objects.requireNonNull(orderId, ORDER_ID_CANNOT_BE_NULL_MESSAGE);
        orderUsecaseFacade.confirmOrder(orderId);
    }

    public void payOrder(Long orderId) {
        Objects.requireNonNull(orderId, ORDER_ID_CANNOT_BE_NULL_MESSAGE);
        orderUsecaseFacade.payOrder(orderId);
    }

    public void pendingPaymentOrder(Long orderId) {
        Objects.requireNonNull(orderId, ORDER_ID_CANNOT_BE_NULL_MESSAGE);
        orderUsecaseFacade.pendingOrder(orderId);
    }

    public Optional<OrderOutput> findById(Long orderId) {
        Objects.requireNonNull(orderId, ORDER_ID_CANNOT_BE_NULL_MESSAGE);
        return this.orderUsecaseFacade.findOrderById(orderId).map(OrderPresenter::toOutput);
    }

    public Page<OrderOutput> findOrderByCurrentUser(Set<StatusOrder> statusOrders, int pageNumber, int pageSize) {
        return this.orderUsecaseFacade
                .findOrderByCurrentUser(Objects.requireNonNullElse(statusOrders, Set.of()), pageNumber, pageSize)
                .mapItems(OrderPresenter::toOutput);
    }
}
