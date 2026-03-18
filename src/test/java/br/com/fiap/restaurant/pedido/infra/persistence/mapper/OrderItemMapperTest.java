package br.com.fiap.restaurant.pedido.infra.persistence.mapper;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.domain.OrderItem;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.MenuItemEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para o mapper OrderItemMapper")
class OrderItemMapperTest {

    @Nested
    @DisplayName("Mapeamento de Domínio para Entidade")
    class DomainToEntity {

        @Test
        @DisplayName("Deve converter um OrderItem de domínio para OrderItemEntity")
        void deveConverterDominioParaEntidade() {
            // Given
            var menuItem = new MenuItem(1L, "Test Item", new BigDecimal("12.50"), false, 10L);
            var orderItem = new OrderItem(100L, menuItem, new BigDecimal("2"), new BigDecimal("12.50"));
            var orderEntity = new OrderEntity();
            orderEntity.setId(200L);

            // When
            OrderItemEntity entity = OrderItemMapper.toOrderItemEntity(orderItem, orderEntity);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(orderItem.getId());
            assertThat(entity.getQuantity()).isEqualTo(orderItem.getQuantity());
            assertThat(entity.getUnitPrice()).isEqualTo(orderItem.getUnitPrice());
            assertThat(entity.getOrder()).isEqualTo(orderEntity);

            assertThat(entity.getMenuItem()).isNotNull();
            assertThat(entity.getMenuItem().getId()).isEqualTo(menuItem.getId());
            assertThat(entity.getMenuItem().getName()).isEqualTo(menuItem.getName());
        }
    }

    @Nested
    @DisplayName("Mapeamento de Entidade para Domínio")
    class EntityToDomain {

        @Test
        @DisplayName("Deve converter um OrderItemEntity para OrderItem de domínio")
        void deveConverterEntidadeParaDominio() {
            // Given
            var menuItemEntity = new MenuItemEntity();
            menuItemEntity.setId(2L);
            menuItemEntity.setName("Entity Item");
            menuItemEntity.setUnitPrice(new BigDecimal("99.99"));
            menuItemEntity.setRestaurantOnly(true);
            menuItemEntity.setRestaurantId(20L);

            var orderItemEntity = new OrderItemEntity();
            orderItemEntity.setId(300L);
            orderItemEntity.setQuantity(new BigDecimal("3"));
            orderItemEntity.setUnitPrice(new BigDecimal("95.00")); // Preço pode ser diferente do item de menu
            orderItemEntity.setMenuItem(menuItemEntity);

            // When
            OrderItem orderItem = OrderItemMapper.toOrderItem(orderItemEntity);

            // Then
            assertThat(orderItem).isNotNull();
            assertThat(orderItem.getId()).isEqualTo(orderItemEntity.getId());
            assertThat(orderItem.getQuantity()).isEqualTo(orderItemEntity.getQuantity());
            assertThat(orderItem.getUnitPrice()).isEqualTo(orderItemEntity.getUnitPrice());

            assertThat(orderItem.getMenuItem()).isNotNull();
            assertThat(orderItem.getMenuItem().getId()).isEqualTo(menuItemEntity.getId());
        }
    }
}
