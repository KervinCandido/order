package br.com.fiap.restaurant.pedido.core.domain;

import br.com.fiap.restaurant.pedido.core.exception.OperationNotAllowedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private final Long id;
    private final Long restaurantId;
    private final UUID customerUuid;
    private final List<OrderItem> items;
    private final LocalDateTime orderDateTime;
    private StatusOrder status;

    public Order(Long id, Long restaurantId, UUID customerUuid, List<OrderItem> items, LocalDateTime orderDateTime, StatusOrder status) {
        this.restaurantId = Objects.requireNonNull(restaurantId, "restaurantId cannot be null.");
        this.customerUuid = Objects.requireNonNull(customerUuid, "customerUuid cannot be null.");
        this.items = new ArrayList<>(Objects.requireNonNull(items, "items cannot be null."));
        this.orderDateTime = Objects.requireNonNull(orderDateTime, "orderDateTime cannot be null.");
        this.status = Objects.requireNonNull(status, "status cannot be null.");
        this.id = id;
    }

    public void confirm() {
        if (!this.status.equals(StatusOrder.CREATED))
            throw new OperationNotAllowedException("Order cannot be confirmed in this situation");
        this.status = StatusOrder.APPROVED;
    }

    public void pendingPay() {
        if (!this.status.equals(StatusOrder.APPROVED))
            throw new OperationNotAllowedException("Order cannot be pending paid in this situation");
        this.status = StatusOrder.PENDING_PAY;
    }

    public void pay() {
        if (!this.status.equals(StatusOrder.APPROVED))
            throw new OperationNotAllowedException("Order cannot be paid in this situation");
        this.status = StatusOrder.PAYED;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.items.add(orderItem);
    }

    public Long getId() {
        return id;
    }

    public UUID getCustomerUuid() {
        return customerUuid;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public StatusOrder getStatus() {
        return status;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public BigDecimal getTotal() {
        return this.items.stream().map(OrderItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
