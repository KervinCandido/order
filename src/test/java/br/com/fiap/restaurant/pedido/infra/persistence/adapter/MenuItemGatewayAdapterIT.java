package br.com.fiap.restaurant.pedido.infra.persistence.adapter;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.MenuItemEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes de integração para o adapter MenuItemGatewayAdapter")
class MenuItemGatewayAdapterIT {

    @Autowired
    private MenuItemRepository menuItemRepository;

    private MenuItemGatewayAdapter menuItemGatewayAdapter;

    @BeforeEach
    void setUp() {
        // Instanciamos o adapter manualmente, injetando o repositório real fornecido pelo @DataJpaTest
        menuItemRepository.deleteAll();
        menuItemGatewayAdapter = new MenuItemGatewayAdapter(menuItemRepository);
    }

    private MenuItemEntity createAndSaveMenuItemEntity(Long id, String name, Long restaurantId) {
        var entity = new MenuItemEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setUnitPrice(BigDecimal.TEN);
        entity.setRestaurantId(restaurantId);
        entity.setRestaurantOnly(false);
        return menuItemRepository.saveAndFlush(entity);
    }

    @Nested
    @DisplayName("Salvamento de Itens de Menu")
    class SaveMenuItems {

        @Test
        @DisplayName("Deve salvar uma lista de itens de menu no banco de dados")
        void deveSalvarListaDeItensDeMenu() {
            // Given
            var menuItem1 = new MenuItem(1L, "Hambúrguer", new BigDecimal("30.50"), false, 1L);
            var menuItem2 = new MenuItem(2L, "Batata Frita", new BigDecimal("15.00"), false, 1L);
            var menuItems = List.of(menuItem1, menuItem2);

            // When
            menuItemGatewayAdapter.saveAll(menuItems);

            // Then
            var savedEntities = menuItemRepository.findAll();
            assertThat(savedEntities).hasSize(2);
            assertThat(savedEntities).extracting(MenuItemEntity::getName).containsExactlyInAnyOrder("Hambúrguer", "Batata Frita");
        }
    }

    @Nested
    @DisplayName("Busca de Itens de Menu")
    class FindMenuItems {

        @Test
        @DisplayName("Deve buscar itens por IDs e mapeá-los para o domínio")
        void deveBuscarItensPorIdsEMapearParaDominio() {
            // Given
            var savedEntity1 = createAndSaveMenuItemEntity(1L, "Item 1", 1L);
            var savedEntity2 = createAndSaveMenuItemEntity(2L, "Item 2", 2L);
            var idsToFind = List.of(savedEntity1.getId(), savedEntity2.getId());

            // When
            List<MenuItem> result = menuItemGatewayAdapter.findAllById(idsToFind);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(MenuItem::getId).containsExactlyInAnyOrder(savedEntity1.getId(), savedEntity2.getId());
            assertThat(result).extracting(MenuItem::getName).containsExactlyInAnyOrder("Item 1", "Item 2");
        }
    }

    @Nested
    @DisplayName("Exclusão de Itens de Menu")
    class DeleteMenuItems {

        @Test
        @DisplayName("Deve deletar um item de menu por ID")
        void deveDeletarItemPorId() {
            // Given
            var savedEntity = createAndSaveMenuItemEntity(1L, "Item a ser deletado", 1L);
            var entityId = savedEntity.getId();
            assertThat(menuItemRepository.existsById(entityId)).isTrue();

            // When
            menuItemGatewayAdapter.deleteById(entityId);

            // Then
            assertThat(menuItemRepository.existsById(entityId)).isFalse();
        }

        @Test
        @DisplayName("Deve deletar todos os itens de menu por ID de restaurante")
        void deveDeletarTodosPorIdDeRestaurante() {
            // Given
            var restaurantIdToDelete = 1L;
            createAndSaveMenuItemEntity(1L, "Item 1 do Restaurante 1", restaurantIdToDelete);
            createAndSaveMenuItemEntity(2L ,"Item 2 do Restaurante 1", restaurantIdToDelete);
            createAndSaveMenuItemEntity(3L, "Item 1 do Restaurante 2", 2L);

            assertThat(menuItemRepository.findAll()).hasSize(3);

            // When
            menuItemGatewayAdapter.deleteAllByRestaurantId(restaurantIdToDelete);

            // Then
            var remainingItems = menuItemRepository.findAll();
            assertThat(remainingItems).hasSize(1);
            assertThat(remainingItems.getFirst().getRestaurantId()).isEqualTo(2L);
        }
    }
}
