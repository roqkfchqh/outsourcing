package com.example.outsourcing.domain.cart.entity;

import com.example.outsourcing.domain.order.entity.Items;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Cart {

    private Long recentShopId;
    private Items items;

    public Cart(List<MenuItem> menuItems) {
        this.items = new Items(menuItems);
    }

    public Cart(Long shopId, List<MenuItem> menuItems) {
        this.recentShopId = shopId;
        this.items = new Items(menuItems);
    }

    public void addItem(Long menuId, Long shopId) {
        // 최근 가게가 아닌 경우, 장바구니 새로 추가
        if (recentShopId != null && !recentShopId.equals(shopId)) {
            items = new Items(new ArrayList<>());
        }
        recentShopId = shopId;
        items.addItem(menuId);
    }

    public void removeItem(Long menuId) {
        items.removeItem(menuId);
    }

    public List<Long> getMenuIds() {
        return items.getMenuIds();
    }

    @Getter
    @AllArgsConstructor
    public static class MenuItem {

        private final Long menuId;
        private int quantity;

        public MenuItem(Long menuId) {
            this.menuId = menuId;
            quantity = 1;
        }

        public void increase() {
            this.quantity += 1;
        }

        public void decrease() {
            this.quantity -= 1;
        }
    }
}
