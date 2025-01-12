package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
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
    private final OrderCartValidator cartValidator;
    private final OrderCartService orderCartService;
    private final OrderFactory orderFactory;

    @Transactional
    public OrderResponseDto createOrder(AuthUser user) {
        Cart cart = orderCartService.getCartData(user.id());
        Map<Long, Menu> menus = cartValidator.validateCartAndReturnMenu(cart);
        BigDecimal totalPrice = getTotalPrice(cart, menus);
        Shop shop = cartValidator.validateShop(cart.getRecentShopId(), totalPrice);
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
        Order order = orderRepository.findOrderByOwner(orderId, user.id())
            .orElseThrow(() -> new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION));
        order.validateIsNotCompleted();
        order.nextStatus();
        return order;
    }

    @Transactional
    public Long rejectOrder(AuthUser user, Long orderId) {
        Order order = orderRepository.findOrderByOwner(orderId, user.id())
            .orElseThrow(() -> new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION));
        order.validateIsPending();
        Long userId = order.getUser().getId();
        orderRepository.deleteById(orderId);
        return userId;
    }

    public OrderResponseDto getOrder(AuthUser user, Long orderId) {
        Order order = findOrder(orderId);
        if (!Objects.equals(order.getUser().getId(), user.id()) &&
            !orderRepository.existsOrderByOwner(orderId, user.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
        Shop shop = order.getShop();
        List<OrderMenuResponseDto> orderMenusDto = order.getOrderMenus().stream()
            .map(OrderMenuMapper::toDto)
            .toList();

        return OrderMapper.toDto(shop.getId(), shop.getName(), order, orderMenusDto);
    }

    public List<OrderResponseDto> getOrdersByShop(AuthUser authUser, Long shopId) {
        Shop shop = findShop(shopId);
        shop.validateIsActive();
        shop.validateOwnership(authUser);
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

    private BigDecimal getTotalPrice(Cart cart, Map<Long, Menu> menus) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Cart.MenuItem item : cart.getItems()) {
            Menu menu = menus.get(item.getMenuId());
            totalPrice = totalPrice.add(
                menu.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
        return totalPrice;
    }

    private Shop findShop(Long shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));
    }
}
