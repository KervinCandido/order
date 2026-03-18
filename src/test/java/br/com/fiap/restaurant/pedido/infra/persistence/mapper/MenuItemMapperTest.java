package br.com.fiap.restaurant.pedido.infra.persistence.mapper;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.MenuItemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para o mapper MenuItemMapper")
class MenuItemMapperTest {

    @Nested
    @DisplayName("Mapeamento de Domínio para Entidade")
    class DomainToEntity {

        @Test
        @DisplayName("Deve converter um MenuItem de domínio para MenuItemEntity")
        void deveConverterDominioParaEntidade() {
            // Given
            var menuItem = new MenuItem(1L, "Test Item", BigDecimal.TEN, false, 10L);

            // When
            MenuItemEntity entity = MenuItemMapper.toMenuItemEntity(menuItem);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(menuItem.getId());
            assertThat(entity.getName()).isEqualTo(menuItem.getName());
            assertThat(entity.getUnitPrice()).isEqualTo(menuItem.getUnitPrice());
            assertThat(entity.getRestaurantId()).isEqualTo(menuItem.getRestaurantId());
        }
    }

    @Nested
    @DisplayName("Mapeamento de Entidade para Domínio")
    class EntityToDomain {

        @Test
        @DisplayName("Deve converter um MenuItemEntity para MenuItem de domínio")
        void deveConverterEntidadeParaDominio() {
            // Given
            var entity = new MenuItemEntity();
            entity.setId(2L);
            entity.setName("Entity Item");
            entity.setUnitPrice(new BigDecimal("99.99"));
            entity.setRestaurantOnly(true);
            entity.setRestaurantId(20L);

            // When
            MenuItem menuItem = MenuItemMapper.toMenuItem(entity);

            // Then
            assertThat(menuItem).isNotNull();
            assertThat(menuItem.getId()).isEqualTo(entity.getId());
            assertThat(menuItem.getName()).isEqualTo(entity.getName());
            assertThat(menuItem.getUnitPrice()).isEqualTo(entity.getUnitPrice());
            assertThat(menuItem.isRestaurantOnly()).isEqualTo(entity.isRestaurantOnly());
            assertThat(menuItem.getRestaurantId()).isEqualTo(entity.getRestaurantId());
        }
    }
}
