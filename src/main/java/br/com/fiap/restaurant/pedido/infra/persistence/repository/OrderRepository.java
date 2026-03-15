package br.com.fiap.restaurant.pedido.infra.persistence.repository;

import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {}
