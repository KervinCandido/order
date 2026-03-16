package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import br.com.fiap.restaurant.pedido.core.outbound.OrderOutput;
import br.com.fiap.restaurant.pedido.core.presenter.OrderPresenter;
import br.com.fiap.restaurant.pedido.core.usecase.order.ConfirmOrderUseCase;
import br.com.fiap.restaurant.pedido.core.usecase.order.CreateOrderUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.order.PayOrderUseCase;
import br.com.fiap.restaurant.pedido.core.usecase.order.PendingOrderUseCase;

import java.util.Objects;

public class OrderController {

    private final CreateOrderUsecase createOrderUsecase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final PendingOrderUseCase pendingOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;

    public OrderController(CreateOrderUsecase createOrderUsecase, ConfirmOrderUseCase confirmOrderUseCase, PendingOrderUseCase pendingOrderUseCase, PayOrderUseCase payOrderUseCase) {
        this.createOrderUsecase = Objects.requireNonNull(createOrderUsecase, "createOrderUsecase cannot be null.");
        this.confirmOrderUseCase = Objects.requireNonNull(confirmOrderUseCase, "confirmOrderUseCase cannot be null.");
        this.pendingOrderUseCase = Objects.requireNonNull(pendingOrderUseCase, "pendingOrderUseCase cannot be null.");
        this.payOrderUseCase = Objects.requireNonNull(payOrderUseCase, "payOrderUseCase cannot be null.");
    }

    public OrderOutput create(CreateOrderInput input) {
        Objects.requireNonNull(input, "createOrderInput cannot be null.");
        var order = createOrderUsecase.create(input);
        return OrderPresenter.toOutput(order);
    }

    public void confirm(Long orderId) {
        Objects.requireNonNull(orderId, "orderId cannot be null.");
        confirmOrderUseCase.confirmOrderBy(orderId);
    }

    public void payOrder(Long orderId) {
        pendingOrderUseCase.pendingOrderById(orderId);
    }

    public void pendingPaymentOrder(Long orderId) {
        payOrderUseCase.payOrderById(orderId);
    }
}
