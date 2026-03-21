package br.com.fiap.restaurant.pedido.infra.persistence.repository;

import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByCustomerUuid(UUID customerUuid, PageRequest pageRequest);
    Page<OrderEntity> findByCustomerUuidAndStatusOrderIn(UUID customerUuid, Set<StatusOrder> statusOrder, PageRequest pageRequest);
}
