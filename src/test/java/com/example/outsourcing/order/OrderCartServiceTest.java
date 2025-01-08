package com.example.outsourcing.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.order.service.OrderCartService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class OrderCartServiceTest {

    @InjectMocks
    private OrderCartService orderCartService;

    @Mock
    private CacheManager cacheManager;

    @Test
    void getCartData_ShouldReturnCart_유효한_값() {
        Cache mockCache = mock(Cache.class);
        Cart cart = new Cart(List.of(new Cart.MenuItem(1L, 2)));

        when(cacheManager.getCache("carts")).thenReturn(mockCache);
        when(mockCache.get(1L, Cart.class)).thenReturn(cart);

        Cart result = orderCartService.getCartData(1L);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getCartData_ShouldThrowException_캐시가_비어있을때() {
        when(cacheManager.getCache("carts")).thenReturn(null);

        assertThrows(InvalidRequestException.class, () -> orderCartService.getCartData(1L));
    }
}

