package br.com.fiap.restaurant.pedido.infra.persistence.adapter;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.mapper.OrderMapper;
import br.com.fiap.restaurant.pedido.infra.persistence.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
}
