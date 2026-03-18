package br.com.fiap.restaurant.pedido.infra.controller;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.infra.config.CoreControllersConfig;
import br.com.fiap.restaurant.pedido.infra.config.CoreGatewayConfig;
import br.com.fiap.restaurant.pedido.infra.config.CoreUsecaseConfig;
import br.com.fiap.restaurant.pedido.infra.controller.request.OrderItemRequest;
import br.com.fiap.restaurant.pedido.infra.controller.request.OrderRequest;
import br.com.fiap.restaurant.pedido.infra.message.publisher.ConfirmOrderPublisher;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.MenuItemEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.repository.MenuItemRepository;
import br.com.fiap.restaurant.pedido.infra.persistence.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@Import({CoreControllersConfig.class, CoreUsecaseConfig.class, CoreGatewayConfig.class})
@AutoConfigureMockMvc
@DisplayName("Testes de integração para o OrderRestController")
class OrderRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @MockitoBean
    private ConfirmOrderPublisher confirmOrderPublisher;

    private static final String USER_ID = "a2c9a844-6d27-4817-8226-8b4309b65ef5";

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        menuItemRepository.deleteAll();
    }

    private OrderEntity createOrderInDatabase(StatusOrder status, String userId) {
        var order = new OrderEntity();
        order.setCustomerUuid(UUID.fromString(userId));
        order.setRestaurantId(1L);
        order.setStatusOrder(status);
        order.setOrderDateTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Nested
    @DisplayName("Criação de Pedido")
    class CreateOrder {

        @Test
        @WithMockUser(username = USER_ID)
        @DisplayName("Deve criar um pedido e retornar 201 Created")
        void deveCriarPedidoComSucesso() throws Exception {
            // Given
            var restaurantId = 1L;
            var menuItem = new MenuItemEntity();
            menuItem.setId(1L);
            menuItem.setName("Hambúrguer");
            menuItem.setUnitPrice(BigDecimal.TEN);
            menuItem.setRestaurantId(restaurantId);
            var savedMenuItem = menuItemRepository.save(menuItem);

            var orderItemRequest = new OrderItemRequest(savedMenuItem.getId(), BigDecimal.ONE);
            var orderRequest = new OrderRequest(List.of(orderItemRequest));

            // When
            mockMvc.perform(post("/{restaurantId}/orders", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").isNumber());

            // Then
            var orders = orderRepository.findAll();
            assertThat(orders).hasSize(1);
            assertThat(orders.getFirst().getCustomerUuid()).isEqualTo(UUID.fromString(USER_ID));
            assertThat(orders.getFirst().getStatusOrder()).isEqualTo(StatusOrder.CREATED);
        }
    }

    @Nested
    @DisplayName("Confirmação de Pedido")
    class ConfirmOrder {

        @Test
        @WithMockUser(username = USER_ID)
        @DisplayName("Deve confirmar um pedido e retornar 204 No Content")
        void deveConfirmarPedidoComSucesso() throws Exception {
            // Given
            var restaurantId = 1L;
            var savedOrder = createOrderInDatabase(StatusOrder.CREATED, USER_ID);

            // When
            mockMvc.perform(post("/{restaurantId}/orders/confirm/{orderId}", restaurantId, savedOrder.getId()))
                    .andExpect(status().isNoContent());

            // Then
            var confirmedOrder = orderRepository.findById(savedOrder.getId());
            assertThat(confirmedOrder).isPresent();
            assertThat(confirmedOrder.get().getStatusOrder()).isEqualTo(StatusOrder.APPROVED);
            then(confirmOrderPublisher).should().publish(any(Order.class));
        }

        @Test
        @WithMockUser(username = "94b163a5-db57-4bfa-8ffd-051f22cd3c2c")
        @DisplayName("Deve retornar 404 Not Found ao tentar confirmar pedido de outro usuário")
        void deveRetornarNotFoundAoConfirmarPedidoDeOutroUsuario() throws Exception {
            // Given
            var savedOrder = createOrderInDatabase(StatusOrder.CREATED, USER_ID);

            // When & Then
            mockMvc.perform(post("/{restaurantId}/orders/confirm/{orderId}", savedOrder.getRestaurantId(), savedOrder.getId()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Current user cannot confirm this order"));
        }

        @Test
        @WithMockUser(username = USER_ID)
        @DisplayName("Deve retornar 422 Unprocessable Content ao tentar confirmar um pedido já pago (PAYED)")
        void deveRetornarBadRequestAoConfirmarPedidoPago() throws Exception {
            // Given
            var savedOrder = createOrderInDatabase(StatusOrder.PAYED, USER_ID);

            // When & Then
            mockMvc.perform(post("/{restaurantId}/orders/confirm/{orderId}", savedOrder.getRestaurantId(), savedOrder.getId()))
                    .andExpect(status().isUnprocessableContent())
                    .andExpect(jsonPath("$.message").value("Order cannot be confirmed in this situation"));
        }

        @Test
        @WithMockUser(username = USER_ID)
        @DisplayName("Deve retornar 422 Unprocessable Content ao tentar confirmar um pedido com pagamento pendente (PENDING_PAY)")
        void deveRetornarBadRequestAoConfirmarPedidoComPagamentoPendente() throws Exception {
            // Given
            var savedOrder = createOrderInDatabase(StatusOrder.PENDING_PAY, USER_ID);

            // When & Then
            mockMvc.perform(post("/{restaurantId}/orders/confirm/{orderId}", savedOrder.getRestaurantId(), savedOrder.getId()))
                    .andExpect(status().isUnprocessableContent())
                    .andExpect(jsonPath("$.message").value("Order cannot be confirmed in this situation"));
        }
    }
}
