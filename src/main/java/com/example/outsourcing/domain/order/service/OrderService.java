package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.cart.entity.OrderMenu;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.entity.Order.Status;
import com.example.outsourcing.domain.order.mapper.OrderMapper;
import com.example.outsourcing.domain.order.mapper.OrderMenuMapper;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderMenuMapper orderMenuMapper;
    private final CacheManager cacheManager;
    private final MenuRepository menuRepository;
    private final ShopRepository shopRepository;

    //캐시(cart) 정보 받아와서 그곳에서 확인
    //해당 주문에 대한 손님 장바구니 데이터 캐시 삭제로직 추가 필요
    @Transactional
    public OrderResponseDto createOrder(AuthUser user) {
        //장바구니 비어있는지 / 캐시데이터 없는지 확인
        Cache cache = cacheManager.getCache("carts");
        if (cache == null) {
            throw new InvalidRequestException(ErrorCode.CART_IS_EMPTY);
        }
        Cart cart = cache.get(user.id(), Cart.class);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new InvalidRequestException(ErrorCode.CART_IS_EMPTY);
        }

        //장바구니 유효성검증
        validateCart(cart);

        Shop shop = shopRepository.findByMenuId(cart.getItems().get(0).getMenuId());

        Order order = Order.of(shop, User.fromAuthUser(user));
        List<OrderMenuResponseDto> orderMenusDto = new ArrayList<>();
        for (Cart.MenuItem item : cart.getItems()) {
            Menu menu = menuRepository.findById(item.getMenuId())
                .orElseThrow(() -> new InvalidRequestException(ErrorCode.MENU_NOT_FOUND));

            OrderMenu orderMenu = OrderMenu.of(
                menu,
                item.getQuantity());
            order.addOrderMenu(orderMenu);
            orderMenusDto.add(orderMenuMapper.toDto(orderMenu));
        }
        orderRepository.save(order);

        cache.evict("carts");   //TODO: 이거 내일 cache 고유 key 없냐고 물어보기

        return orderMapper.toDto(order, orderMenusDto);
    }

    @Transactional
    public void toNextStatus(AuthUser user, Long orderId) {
        //해당 주문을 받은 가게의 사장님인지 확인
        Order order = orderRepository.findOrderByOwner(orderId, user.id())
            .orElseThrow(() -> new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION));

        //다음 상태로 변경
        order.nextStatus();
    }

    public OrderResponseDto getOrder(AuthUser user, Long orderId) {
        Order order = findOrder(orderId);

        //주문한사람 또는 가게 사장님인지 확인
        if (!order.getUser().getId().equals(user.id()) && !orderRepository.existsOrderByOwner(
            orderId, user.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }

        List<OrderMenuResponseDto> orderMenusDto = order.getOrderMenus().stream()
            .map(orderMenuMapper::toDto)
            .toList();

        return orderMapper.toDto(order, orderMenusDto);
    }

    @Transactional
    public void rejectOrder(AuthUser user, Long orderId) {
        //해당 주문을 받은 가게의 사장님인지 확인
        Order order = orderRepository.findOrderByOwner(orderId, user.id())
            .orElseThrow(() -> new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION));
        //보류 중인 요청인지 확인
        if (order.getStatus() != Status.PENDING) {
            throw new InvalidRequestException(ErrorCode.CANNOT_CHANGE_STATUS);
        }
        orderRepository.deleteById(orderId);
    }

    public List<OrderResponseDto> getOrdersByShop(AuthUser authUser, Long shopId) {
        // 가게 확인 (가게가 존재하는지)
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));

        // 가게가 폐업 상태인지 확인
        if (shop.isDeleted()) {
            throw new InvalidRequestException(ErrorCode.SHOP_DELETED);
        }

        // 사장님이 해당 가게의 소유자인지 확인
        if (!shop.getUser().getId().equals(authUser.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }

        List<Order> orders = orderRepository.findAllByShopId(shopId);

        return orders.stream()
            .map(order -> {
                List<OrderMenuResponseDto> orderMenusDto = order.getOrderMenus().stream()
                    .map(orderMenuMapper::toDto)
                    .toList();
                return orderMapper.toDto(order, orderMenusDto);
            })
            .toList();
    }

    /*
    helper
     */
    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.ORDER_NOT_FOUND));
    }

    private void validateCart(Cart cart) {
        if (cart.getItems().isEmpty()) {
            throw new InvalidRequestException(ErrorCode.CART_IS_EMPTY);
        }

        // 첫 번째 메뉴의 Shop 가져오기
        Cart.MenuItem firstItem = cart.getItems().get(0);
        Menu firstMenu = menuRepository.findById(firstItem.getMenuId())
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.MENU_NOT_FOUND));

        Shop shop = firstMenu.getShop();

        // 가게 유효성 검사 (운영 시간, 폐업 여부)
        validateShop(shop);

        // 메뉴와 가격 검증, 동일한 가게인지 확인
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart.MenuItem item : cart.getItems()) {
            Menu menu = menuRepository.findById(item.getMenuId())
                .orElseThrow(() -> new InvalidRequestException(ErrorCode.MENU_NOT_FOUND));

            if (!menu.getShop().getId().equals(shop.getId())) {
                throw new InvalidRequestException(ErrorCode.DIFFERENT_SHOP);
            }

            totalAmount = totalAmount.add(
                menu.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 최소 주문 금액 검증
        if (totalAmount.compareTo(shop.getMinOrderPrice()) < 0) {
            throw new InvalidRequestException(ErrorCode.MINIMUM_ORDER_NOT_MET);
        }
    }

    private void validateShop(Shop shop) {
        LocalTime now = LocalTime.now();
        if (now.isBefore(shop.getOpen().toLocalTime()) || now.isAfter(
            shop.getClose().toLocalTime())) {
            throw new InvalidRequestException(ErrorCode.SHOP_CLOSED);
        }

        if (shop.isDeleted()) {
            throw new InvalidRequestException(ErrorCode.SHOP_DELETED);
        }
    }
}
