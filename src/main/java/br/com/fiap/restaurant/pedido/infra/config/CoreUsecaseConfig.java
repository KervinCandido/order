package br.com.fiap.restaurant.pedido.infra.config;

import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.UpdateAllMenuItemsUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.order.ConfirmOrderUseCase;
import br.com.fiap.restaurant.pedido.core.usecase.order.CreateOrderUsecase;
import br.com.fiap.restaurant.pedido.infra.message.publisher.ConfirmOrderPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CoreUsecaseConfig {

    @Bean
    UpdateAllMenuItemsUsecase updateAllMenuItemsUsecase(MenuItemGateway menuItemGateway) {
        return new UpdateAllMenuItemsUsecase(menuItemGateway);
    }

    @Bean
    CreateOrderUsecase createOrderUsecase(MenuItemGateway menuItemGateway, OrderGateway orderGateway, LoggedUserGateway loggedUserGateway) {
        return new CreateOrderUsecase(menuItemGateway, orderGateway, loggedUserGateway);
    }

    @Bean
    ConfirmOrderUseCase confirmOrderUseCase(LoggedUserGateway loggedUserGateway, OrderGateway orderGateway, ConfirmOrderPublisher confirmOrderPublisher) {
        return new ConfirmOrderUseCase(loggedUserGateway, orderGateway, confirmOrderPublisher);
    }
}
