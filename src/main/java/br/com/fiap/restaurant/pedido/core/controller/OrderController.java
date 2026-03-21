package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import br.com.fiap.restaurant.pedido.core.outbound.OrderOutput;
import br.com.fiap.restaurant.pedido.core.presenter.OrderPresenter;
import br.com.fiap.restaurant.pedido.core.usecase.order.*;

import java.util.Objects;
import java.util.Optional;

public class OrderController {

    public static final String ORDER_ID_CANNOT_BE_NULL_MESSAGE = "orderId cannot be null";

    private final CreateOrderUsecase createOrderUsecase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final PendingOrderUseCase pendingOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;
    private final FindOrderByIdUsecase findOrderByIdUsecase;

    public OrderController(CreateOrderUsecase createOrderUsecase, ConfirmOrderUseCase confirmOrderUseCase, PendingOrderUseCase pendingOrderUseCase, PayOrderUseCase payOrderUseCase, FindOrderByIdUsecase findOrderByIdUsecase) {
        this.createOrderUsecase = Objects.requireNonNull(createOrderUsecase, "createOrderUsecase cannot be null.");
        this.confirmOrderUseCase = Objects.requireNonNull(confirmOrderUseCase, "confirmOrderUseCase cannot be null.");
        this.pendingOrderUseCase = Objects.requireNonNull(pendingOrderUseCase, "pendingOrderUseCase cannot be null.");
        this.payOrderUseCase = Objects.requireNonNull(payOrderUseCase, "payOrderUseCase cannot be null.");
        this.findOrderByIdUsecase = Objects.requireNonNull(findOrderByIdUsecase, "findOrderByIdUsecase cannot be null.");
    }

    public OrderOutput create(CreateOrderInput input) {
        Objects.requireNonNull(input, "createOrderInput cannot be null.");
        var order = createOrderUsecase.create(input);
        return OrderPresenter.toOutput(order);
    }

    public void confirm(Long orderId) {
        Objects.requireNonNull(orderId, ORDER_ID_CANNOT_BE_NULL_MESSAGE);
        confirmOrderUseCase.confirmOrderBy(orderId);
    }

    public void payOrder(Long orderId) {
        Objects.requireNonNull(orderId, ORDER_ID_CANNOT_BE_NULL_MESSAGE);
        payOrderUseCase.payOrderById(orderId);
    }

    public void pendingPaymentOrder(Long orderId) {
        Objects.requireNonNull(orderId, ORDER_ID_CANNOT_BE_NULL_MESSAGE);
        pendingOrderUseCase.pendingOrderById(orderId);
    }

    public Optional<OrderOutput> findById(Long orderId) {
        Objects.requireNonNull(orderId, ORDER_ID_CANNOT_BE_NULL_MESSAGE);
        return this.findOrderByIdUsecase.findById(orderId).map(OrderPresenter::toOutput);
    }
}
