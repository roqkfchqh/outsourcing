package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.cart.entity.Cart;
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
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final OrderCartValidation validator;
    private final OrderCartService orderCartService;
    private final OrderFactory orderFactory;

    // 캐시(cart) 정보 받아와서 검증 후 주문 create
    @Transactional
    public OrderResponseDto createOrder(AuthUser user) {
        Cart cart = orderCartService.getCartData(user.id());
        Map<Long, Menu> menus = validator.validateCartAndReturnMenu(cart);
        BigDecimal totalPrice = getTotalPrice(cart, menus);
        Shop shop = validator.validateShop(cart.getRecentShopId(), totalPrice);
        Order order = orderFactory.createOrder(User.fromAuthUser(user), totalPrice, menus,
            cart.getItems());

        orderRepository.save(order);
        orderCartService.evictCartData(user.id());

        List<OrderMenuResponseDto> orderMenusDto = order.getOrderMenus().stream()
            .map(OrderMenuMapper::toDto)
            .toList();
        return OrderMapper.toDto(shop.getId(), shop.getUser().getId(), shop.getName(), order,
            orderMenusDto);
    }

    @Transactional
    public Order toNextStatus(AuthUser user, Long orderId) {
        // 해당 주문을 받은 가게의 사장님인지 확인
        Order order = orderRepository.findOrderByOwner(orderId, user.id())
            .orElseThrow(() -> new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION));

        // 다음 상태로 변경
        order.nextStatus();
        return order;
    }

    @Transactional
    public Long rejectOrder(AuthUser user, Long orderId) {
        // 해당 주문을 받은 가게의 사장님인지 확인
        Order order = orderRepository.findOrderByOwner(orderId, user.id())
            .orElseThrow(() -> new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION));
        // 보류 중인 요청인지 확인
        if (order.getStatus() != Status.PENDING) {
            throw new InvalidRequestException(ErrorCode.CANNOT_CHANGE_STATUS);
        }
        Long userId = order.getUser().getId();
        orderRepository.deleteById(orderId);
        return userId;
    }

    public OrderResponseDto getOrder(AuthUser user, Long orderId) {
        Order order = findOrder(orderId);

        //주문한 사람도, 가게 사장님도 아닐 경우 throw error
        if (!Objects.equals(order.getUser().getId(), user.id()) &&
            !orderRepository.existsOrderByOwner(orderId, user.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }

        Shop shop = order.getOrderMenus().stream()
            .findFirst()
            .map(orderMenu -> orderMenu.getMenu().getShop())
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));

        List<OrderMenuResponseDto> orderMenusDto = order.getOrderMenus().stream()
            .map(OrderMenuMapper::toDto)
            .toList();

        return OrderMapper.toDto(shop.getId(), shop.getName(), order, orderMenusDto);
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
                    .map(OrderMenuMapper::toDto)
                    .toList();
                return OrderMapper.toDto(shop.getId(), shop.getName(), order, orderMenusDto);
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

    private static BigDecimal getTotalPrice(Cart cart, Map<Long, Menu> menus) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Cart.MenuItem item : cart.getItems()) {
            Menu menu = menus.get(item.getMenuId());
            totalPrice = totalPrice.add(
                menu.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
        return totalPrice;
    }
}
