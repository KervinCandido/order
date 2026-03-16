package br.com.fiap.restaurant.pedido.infra.config;

import br.com.fiap.restaurant.pedido.core.controller.MenuItemController;
import br.com.fiap.restaurant.pedido.core.controller.OrderController;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.DeleteAllMenuItemsByRestaurantIdUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.DeleteMenuItemUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.SaveAllMenuItemsUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.order.ConfirmOrderUseCase;
import br.com.fiap.restaurant.pedido.core.usecase.order.CreateOrderUsecase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreControllersConfig {

    @Bean
    MenuItemController menuItemController(SaveAllMenuItemsUsecase saveAllMenuItemsUsecase,
                                          DeleteAllMenuItemsByRestaurantIdUsecase deleteAllMenuItemsByRestaurantIdUsecase,
                                          DeleteMenuItemUsecase deleteMenuItemUsecase) {
        return new MenuItemController(saveAllMenuItemsUsecase, deleteAllMenuItemsByRestaurantIdUsecase, deleteMenuItemUsecase);
    }

    @Bean
    OrderController orderController(CreateOrderUsecase createOrderUsecase, ConfirmOrderUseCase confirmOrderUseCase) {
        return new OrderController(createOrderUsecase, confirmOrderUseCase);
    }
}
