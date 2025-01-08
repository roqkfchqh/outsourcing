package com.example.outsourcing.domain.cart.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Cart {

	private Long recentShopId;
	private List<MenuItem> items = new ArrayList<>();

	public Cart(List<MenuItem> items) {
		this.items = items;
	}

	public void addItem(Long menuId, Long shopId) {
		// 최근 가게가 아닌 경우, 장바구니 새로 추가
		if (recentShopId != null && !recentShopId.equals(shopId)) {
			items.clear();
		}
		recentShopId = shopId;

		items.stream()
			.filter(item -> item.getMenuId().equals(menuId))
			.findFirst()
			.ifPresentOrElse(
				MenuItem::increase,
				() -> items.add(new MenuItem(menuId))
			);
	}

	@Getter
	public static class MenuItem {

		private final Long menuId;
		private int quantity;

		private MenuItem(Long menuId) {
			this.menuId = menuId;
			quantity = 1;
		}

		private void increase() {
			this.quantity += 1;
		}
	}
        private Long menuId;
        private int quantity;

        public MenuItem(Long menuId, int quantity) {
            this.menuId = menuId;
            this.quantity = quantity;
        }
    }
}
