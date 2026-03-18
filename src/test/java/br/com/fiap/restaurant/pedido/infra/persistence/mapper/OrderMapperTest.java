package br.com.fiap.restaurant.pedido.infra.persistence.mapper;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.OrderItem;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.MenuItemEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para o mapper OrderMapper")
class OrderMapperTest {

    @Nested
    @DisplayName("Mapeamento de Domínio para Entidade")
    class DomainToEntity {

        @Test
        @DisplayName("Deve converter um Order de domínio para OrderEntity")
        void deveConverterDominioParaEntidade() {
            // Given
            var menuItem = new MenuItem(1L, "Item", BigDecimal.TEN, false, 1L);
            var orderItem = new OrderItem(10L, menuItem, BigDecimal.ONE, BigDecimal.TEN);
            var order = new Order(100L, 1L, UUID.randomUUID(), List.of(orderItem), LocalDateTime.now(), StatusOrder.CREATED);

            // When
            OrderEntity entity = OrderMapper.toOrderEntity(order);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(order.getId());
            assertThat(entity.getRestaurantId()).isEqualTo(order.getRestaurantId());
            assertThat(entity.getCustomerUuid()).isEqualTo(order.getCustomerUuid());
            assertThat(entity.getOrderDateTime()).isEqualTo(order.getOrderDateTime());
            assertThat(entity.getStatusOrder()).isEqualTo(order.getStatus());

            assertThat(entity.getOrderItems()).hasSize(1);
            assertThat(entity.getOrderItems().getFirst().getId()).isEqualTo(orderItem.getId());
            // Garante que a referência circular foi criada corretamente
            assertThat(entity.getOrderItems().getFirst().getOrder()).isEqualTo(entity);
        }
    }

    @Nested
    @DisplayName("Mapeamento de Entidade para Domínio")
    class EntityToDomain {

        @Test
        @DisplayName("Deve converter um OrderEntity para Order de domínio")
        void deveConverterEntidadeParaDominio() {
            // Given
            var menuItemEntity = new MenuItemEntity();
            menuItemEntity.setId(2L);
            menuItemEntity.setName("Entity Item");
            menuItemEntity.setUnitPrice(new BigDecimal("99.99"));

            var orderItemEntity = new OrderItemEntity();
            orderItemEntity.setId(20L);
            orderItemEntity.setQuantity(BigDecimal.TEN);
            orderItemEntity.setUnitPrice(new BigDecimal("99.99"));
            orderItemEntity.setMenuItem(menuItemEntity);

            var orderEntity = new OrderEntity();
            orderEntity.setId(200L);
            orderEntity.setRestaurantId(2L);
            orderEntity.setCustomerUuid(UUID.randomUUID());
            orderEntity.setOrderDateTime(LocalDateTime.now());
            orderEntity.setStatusOrder(StatusOrder.APPROVED);
            orderEntity.setOrderItems(List.of(orderItemEntity));

            // When
            Order order = OrderMapper.toOrder(orderEntity);

            // Then
            assertThat(order).isNotNull();
            assertThat(order.getId()).isEqualTo(orderEntity.getId());
            assertThat(order.getRestaurantId()).isEqualTo(orderEntity.getRestaurantId());
            assertThat(order.getCustomerUuid()).isEqualTo(orderEntity.getCustomerUuid());
            assertThat(order.getOrderDateTime()).isEqualTo(orderEntity.getOrderDateTime());
            assertThat(order.getStatus()).isEqualTo(orderEntity.getStatusOrder());

            assertThat(order.getItems()).hasSize(1);
            assertThat(order.getItems().getFirst().getId()).isEqualTo(orderItemEntity.getId());
        }
    }
}
