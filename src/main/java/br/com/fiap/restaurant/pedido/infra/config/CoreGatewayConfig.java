package br.com.fiap.restaurant.pedido.infra.config;

import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
import br.com.fiap.restaurant.pedido.infra.auth.LoggedUserGatewayAdapter;
import br.com.fiap.restaurant.pedido.infra.persistence.adapter.MenuItemGatewayAdapter;
import br.com.fiap.restaurant.pedido.infra.persistence.adapter.OrderGatewayAdapter;
import br.com.fiap.restaurant.pedido.infra.persistence.repository.MenuItemRepository;
import br.com.fiap.restaurant.pedido.infra.persistence.repository.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreGatewayConfig {

    @Bean
    MenuItemGateway menuItemGateway(MenuItemRepository menuItemRepository) {
        return new MenuItemGatewayAdapter(menuItemRepository);
    }

    @Bean
    OrderGateway orderGateway(OrderRepository orderRepository) {
        return new OrderGatewayAdapter(orderRepository);
    }

    @Bean
    LoggedUserGateway loggedUserGateway() {
        return new LoggedUserGatewayAdapter();
    }
}
