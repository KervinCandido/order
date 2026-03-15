package br.com.fiap.restaurant.pedido.infra.config;

import br.com.fiap.restaurant.pedido.core.controller.MenuItemController;
import br.com.fiap.restaurant.pedido.core.controller.OrderController;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.DeleteAllMenuItemsByRestaurantIdUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.UpdateAllMenuItemsUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.order.ConfirmOrderUseCase;
import br.com.fiap.restaurant.pedido.core.usecase.order.CreateOrderUsecase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreControllersConfig {

    @Bean
    MenuItemController menuItemController(UpdateAllMenuItemsUsecase updateAllMenuItemsUsecase,
                                          DeleteAllMenuItemsByRestaurantIdUsecase deleteAllMenuItemsByRestaurantIdUsecase) {
        return new MenuItemController(updateAllMenuItemsUsecase, deleteAllMenuItemsByRestaurantIdUsecase);
    }

    @Bean
    OrderController orderController(CreateOrderUsecase createOrderUsecase, ConfirmOrderUseCase confirmOrderUseCase) {
        return new OrderController(createOrderUsecase, confirmOrderUseCase);
    }
}
