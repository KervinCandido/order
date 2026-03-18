package br.com.fiap.restaurant.pedido.infra.persistence.adapter;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.OrderItem;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.MenuItemEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.repository.MenuItemRepository;
import br.com.fiap.restaurant.pedido.infra.persistence.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes de integração para o adapter OrderGatewayAdapter")
class OrderGatewayAdapterIT {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;

    private OrderGatewayAdapter orderGatewayAdapter;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        menuItemRepository.deleteAll();
        orderGatewayAdapter = new OrderGatewayAdapter(orderRepository);
    }

    private MenuItemEntity createAndSaveMenuItem(Long id, String name, Long restaurantId) {
        var menuItemEntity = new MenuItemEntity();
        menuItemEntity.setId(id);
        menuItemEntity.setName(name);
        menuItemEntity.setUnitPrice(BigDecimal.TEN);
        menuItemEntity.setRestaurantId(restaurantId);
        return menuItemRepository.saveAndFlush(menuItemEntity);
    }

    @Nested
    @DisplayName("Salvamento de Pedido")
    class SaveOrder {

        @Test
        @DisplayName("Deve salvar um pedido de domínio no banco de dados")
        void deveSalvarPedidoDeDominio() {
            // Given
            var hamburger = createAndSaveMenuItem(1L, "Hambúrguer", 1L);
            var miniPizza = createAndSaveMenuItem(2L, "Mini Pizza", 1L);
            var menuItemHamburger = new MenuItem(hamburger.getId(), hamburger.getName(), hamburger.getUnitPrice(), hamburger.isRestaurantOnly(), hamburger.getRestaurantId());
            var menuItemMiniPizza = new MenuItem(miniPizza.getId(), miniPizza.getName(), miniPizza.getUnitPrice(), miniPizza.isRestaurantOnly(), miniPizza.getRestaurantId());
            var orderItemHamburger = new OrderItem(menuItemHamburger, new BigDecimal("2"));
            var orderItemMiniPizza = new OrderItem(menuItemMiniPizza, new BigDecimal("2"));
            var order = new Order(null, 1L, UUID.randomUUID(), List.of(orderItemHamburger, orderItemMiniPizza), LocalDateTime.now(), StatusOrder.CREATED);

            // When
            Order savedOrder = orderGatewayAdapter.save(order);

            // Then
            assertThat(savedOrder.getId()).isNotNull();
            var foundEntity = orderRepository.findById(savedOrder.getId());
            assertThat(foundEntity).isPresent();
            assertThat(foundEntity.get().getCustomerUuid()).isEqualTo(order.getCustomerUuid());
            assertThat(foundEntity.get().getOrderItems()).hasSize(2);
            assertThat(foundEntity.get().getOrderItems().getFirst().getMenuItem().getId()).isEqualTo(menuItemHamburger.getId());
            assertThat(foundEntity.get().getOrderItems().get(1).getMenuItem().getId()).isEqualTo(menuItemMiniPizza.getId());
        }
    }

    @Nested
    @DisplayName("Busca de Pedido por ID")
    class FindOrderById {

        @Test
        @DisplayName("Deve buscar um pedido por ID e mapeá-lo para o domínio")
        void deveBuscarPedidoPorIdEMapearParaDominio() {
            // Given
            var orderEntity = new OrderEntity();
            orderEntity.setCustomerUuid(UUID.randomUUID());
            orderEntity.setRestaurantId(1L);
            orderEntity.setStatusOrder(StatusOrder.CREATED);
            orderEntity.setOrderDateTime(LocalDateTime.now());
            orderEntity.setOrderItems(List.of());
            var savedEntity = orderRepository.save(orderEntity);
            var orderId = savedEntity.getId();

            // When
            Optional<Order> result = orderGatewayAdapter.findById(orderId);

            // Then
            assertThat(result).isPresent();
            Order foundOrder = result.get();
            assertThat(foundOrder.getId()).isEqualTo(orderId);
            assertThat(foundOrder.getCustomerUuid()).isEqualTo(savedEntity.getCustomerUuid());
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando o ID do pedido não existir")
        void deveRetornarOptionalVazioQuandoIdNaoExistir() {
            // Given
            var nonExistentId = 999L;

            // When
            Optional<Order> result = orderGatewayAdapter.findById(nonExistentId);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando o ID do pedido for nulo")
        void deveRetornarOptionalVazioQuandoIdForNulo() {
            // When
            Optional<Order> result = orderGatewayAdapter.findById(null);

            // Then
            assertThat(result).isEmpty();
        }
    }
}
