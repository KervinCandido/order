package br.com.fiap.restaurant.pedido.infra.persistence.adapter;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.domain.pagination.Page;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.mapper.OrderMapper;
import br.com.fiap.restaurant.pedido.infra.persistence.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class OrderGatewayAdapter implements OrderGateway {

    private final OrderRepository orderRepository;

    public OrderGatewayAdapter(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity orderEntity = OrderMapper.toOrderEntity(order);
        return OrderMapper.toOrder(orderRepository.save(orderEntity));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Order> findById(Long orderId) {
        if (orderId == null) {
            return Optional.empty();
        }
        return orderRepository.findById(orderId).map(OrderMapper::toOrder);
    }

    @Override
    public Page<Order> findByUser(UUID userUuid, int pageNumber, int pageSize) {
        Objects.requireNonNull(userUuid, "userUuid cannot be null");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "orderDateTime");
        var ordersPage = orderRepository.findByCustomerUuid(userUuid, pageRequest);

        return new Page<>(
            ordersPage.getNumber(),
            ordersPage.getSize(),
            ordersPage.getTotalElements(),
            ordersPage.getContent().stream().map(OrderMapper::toOrder).toList()
        );
    }

    @Override
    public Page<Order> findByUserAndStatus(UUID userUuid, Set<StatusOrder> orderStatus, int pageNumber, int pageSize) {
        Objects.requireNonNull(userUuid, "userUuid cannot be null");

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "orderDateTime");
        var ordersPage = orderRepository.findByCustomerUuidAndStatusOrderIn(userUuid, Objects.requireNonNullElse(orderStatus, Set.of()), pageRequest);

        return new Page<>(
                ordersPage.getNumber(),
                ordersPage.getSize(),
                ordersPage.getTotalElements(),
                ordersPage.getContent().stream().map(OrderMapper::toOrder).toList()
        );
    }
}
