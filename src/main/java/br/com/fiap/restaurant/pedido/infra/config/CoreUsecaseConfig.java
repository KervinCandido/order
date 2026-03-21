package br.com.fiap.restaurant.pedido.infra.config;

import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.DeleteAllMenuItemsByRestaurantIdUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.DeleteMenuItemUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.SaveAllMenuItemsUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.order.*;
import br.com.fiap.restaurant.pedido.infra.message.publisher.CreatedOrderPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreUsecaseConfig {

    @Bean
    SaveAllMenuItemsUsecase updateAllMenuItemsUsecase(MenuItemGateway menuItemGateway) {
        return new SaveAllMenuItemsUsecase(menuItemGateway);
    }

    @Bean
    DeleteAllMenuItemsByRestaurantIdUsecase deleteAllMenuItemsByRestaurantIdUsecase(MenuItemGateway menuItemGateway) {
        return new DeleteAllMenuItemsByRestaurantIdUsecase(menuItemGateway);
    }

    @Bean
    DeleteMenuItemUsecase deleteMenuItemUsecase(MenuItemGateway menuItemGateway) {
        return new DeleteMenuItemUsecase(menuItemGateway);
    }

    @Bean
    CreateOrderUsecase createOrderUsecase(MenuItemGateway menuItemGateway, OrderGateway orderGateway, LoggedUserGateway loggedUserGateway) {
        return new CreateOrderUsecase(menuItemGateway, orderGateway, loggedUserGateway);
    }

    @Bean
    ConfirmOrderUseCase confirmOrderUseCase(LoggedUserGateway loggedUserGateway, OrderGateway orderGateway, CreatedOrderPublisher createdOrderPublisher) {
        return new ConfirmOrderUseCase(loggedUserGateway, orderGateway, createdOrderPublisher);
    }

    @Bean
    PendingOrderUseCase pendingOrderUseCase(OrderGateway orderGateway) {
        return new PendingOrderUseCase(orderGateway);
    }

    @Bean
    PayOrderUseCase payOrderUseCase(OrderGateway orderGateway) {
        return new PayOrderUseCase(orderGateway);
    }

    @Bean
    FindOrderByIdUsecase findOrderByIdUsecase(LoggedUserGateway loggedUserGateway, OrderGateway orderGateway) {
        return new FindOrderByIdUsecase(loggedUserGateway, orderGateway);
    }
}
