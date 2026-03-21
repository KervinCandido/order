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
import java.util.Set;
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
            var order = new Order(null, 1L, UUID.randomUUID(), List.of(orderItemHamburger, orderItemMiniPizza), LocalDateTime.now(), StatusOrder.DRAFT);

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
            orderEntity.setStatusOrder(StatusOrder.DRAFT);
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

    @Nested
    @DisplayName("Busca de Pedidos por Usuário")
    class FindOrdersByUser {

        private final int pageNumber = 0;
        private final int pageSize = 10;

        @Test
        @DisplayName("Deve retornar uma lista de pedidos para um determinado usuário")
        void deveRetornarListaDePedidosPorUsuario() {
            // Given
            var userUuid = UUID.randomUUID();
            var orderEntity1 = new OrderEntity();
            orderEntity1.setCustomerUuid(userUuid);
            orderEntity1.setRestaurantId(1L);
            orderEntity1.setStatusOrder(StatusOrder.DRAFT);
            orderEntity1.setOrderDateTime(LocalDateTime.now());
            orderEntity1.setOrderItems(List.of());
            orderRepository.save(orderEntity1);

            var orderEntity2 = new OrderEntity();
            orderEntity2.setCustomerUuid(userUuid);
            orderEntity2.setRestaurantId(1L);
            orderEntity2.setStatusOrder(StatusOrder.CREATED);
            orderEntity2.setOrderDateTime(LocalDateTime.now());
            orderEntity2.setOrderItems(List.of());
            orderRepository.save(orderEntity2);

            // When
            var result = orderGatewayAdapter.findByUser(userUuid, pageNumber, pageSize);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(pageNumber);
            assertThat(result.pageSize()).isEqualTo(pageSize);
            assertThat(result.totalElements()).isEqualTo(2);
            assertThat(result.content()).hasSize(2);
            assertThat(result.content().getFirst().getCustomerUuid()).isEqualTo(userUuid);
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia se o usuário não tiver pedidos")
        void deveRetornarListaVaziaSeUsuarioNaoTiverPedidos() {
            // Given
            var userUuid = UUID.randomUUID();

            // When
            var result = orderGatewayAdapter.findByUser(userUuid, pageNumber, pageSize);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(pageNumber);
            assertThat(result.pageSize()).isEqualTo(pageSize);
            assertThat(result.totalElements()).isZero();
            assertThat(result.content()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Busca de Pedidos por Usuário e Status")
    class FindOrdersByUserAndStatus {

        private final int pageNumber = 0;
        private final int pageSize = 10;
        private final Set<StatusOrder> statusOrders = Set.of(StatusOrder.CREATED, StatusOrder.DRAFT);

        @Test
        @DisplayName("Deve retornar uma lista de pedidos para um usuário com um status específico")
        void deveRetornarListaDePedidosPorUsuarioEStatus() {
            // Given
            var userUuid = UUID.randomUUID();
            var status = StatusOrder.CREATED;

            var orderEntity1 = new OrderEntity();
            orderEntity1.setCustomerUuid(userUuid);
            orderEntity1.setRestaurantId(1L);
            orderEntity1.setStatusOrder(status);
            orderEntity1.setOrderDateTime(LocalDateTime.now());
            orderEntity1.setOrderItems(List.of());
            orderRepository.save(orderEntity1);

            var orderEntity2 = new OrderEntity();
            orderEntity2.setCustomerUuid(userUuid);
            orderEntity2.setRestaurantId(1L);
            orderEntity2.setStatusOrder(StatusOrder.DRAFT);
            orderEntity2.setOrderDateTime(LocalDateTime.now());
            orderEntity2.setOrderItems(List.of());
            orderRepository.save(orderEntity2);

            // When
            var result = orderGatewayAdapter.findByUserAndStatus(userUuid, statusOrders, pageNumber, pageSize);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(pageNumber);
            assertThat(result.pageSize()).isEqualTo(pageSize);
            assertThat(result.totalElements()).isEqualTo(2);
            assertThat(result.content()).hasSize(2);
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia se não houver pedidos com o status especificado")
        void deveRetornarListaVaziaSeNaoHouverPedidosComStatus() {
            // Given
            var userUuid = UUID.randomUUID();
            var status = StatusOrder.CREATED;

            var orderEntity1 = new OrderEntity();
            orderEntity1.setCustomerUuid(userUuid);
            orderEntity1.setRestaurantId(1L);
            orderEntity1.setStatusOrder(StatusOrder.DRAFT);
            orderEntity1.setOrderDateTime(LocalDateTime.now());
            orderEntity1.setOrderItems(List.of());
            orderRepository.save(orderEntity1);

            // When
            var result = orderGatewayAdapter.findByUserAndStatus(userUuid, Set.of(status), pageNumber, pageSize);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.pageNumber()).isEqualTo(pageNumber);
            assertThat(result.pageSize()).isEqualTo(pageSize);
            assertThat(result.totalElements()).isZero();
            assertThat(result.content()).isEmpty();
        }
    }
}
