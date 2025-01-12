package com.example.outsourcing.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.entity.OrderMenu;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.review.dto.ReviewRequestDto;
import com.example.outsourcing.domain.review.dto.UserReviewResponseDto;
import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.review.service.ReviewService;
import com.example.outsourcing.domain.review.service.ReviewValidator;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CreateReviewTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReviewValidator validator;

    @Test
    void createReview_ShouldSaveReview_유효한_값() {
        AuthUser user = new AuthUser(1L, "testUser", UserRole.USER);
        Long orderId = 1L;

        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        ReflectionTestUtils.setField(shop, "name", "Test Shop");
        ReflectionTestUtils.setField(shop, "minOrderPrice", BigDecimal.valueOf(50));

        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", 1L);
        ReflectionTestUtils.setField(menu, "name", "Test Menu");
        ReflectionTestUtils.setField(menu, "price", BigDecimal.TEN);
        ReflectionTestUtils.setField(menu, "shop", shop);

        OrderMenu orderMenu = new OrderMenu(menu, 2);
        Order order = new Order();
        ReflectionTestUtils.setField(order, "user", User.fromAuthUser(user));
        ReflectionTestUtils.setField(order, "status", Order.Status.COMPLETED);
        ReflectionTestUtils.setField(order, "orderMenus", List.of(orderMenu));

        ReviewRequestDto dto = new ReviewRequestDto("개맛있네", 5);
        Review review = Review.of(User.fromAuthUser(user), shop, order, dto.content(),
            dto.rating());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        UserReviewResponseDto response = reviewService.createReview(user, orderId, dto);

        assertNotNull(response);
        assertEquals("개맛있네", response.content());
        assertEquals(5, response.rating());
    }

    @Test
    void createReview_ShouldThrowInvalidRequestException_해당_주문에대한_리뷰가_이미_존재할때() {
        AuthUser user = new AuthUser(1L, "testUser", UserRole.USER);
        Long orderId = 1L;
        Order order = new Order();
        ReflectionTestUtils.setField(order, "user", User.fromAuthUser(user));
        ReflectionTestUtils.setField(order, "status", Order.Status.COMPLETED);
        ReflectionTestUtils.setField(order, "orderMenus", List.of());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ReviewRequestDto dto = new ReviewRequestDto("개맛있네", 5);

        assertThrows(InvalidRequestException.class,
            () -> reviewService.createReview(user, orderId, dto));
    }

    @Test
    void createReview_ShouldThrowInvalidRequestException_주문이_완료된_상태가_아닐때() {
        AuthUser user = new AuthUser(1L, "testUser", UserRole.USER);
        Long orderId = 1L;
        Order order = new Order();
        ReflectionTestUtils.setField(order, "user", User.fromAuthUser(user));
        ReflectionTestUtils.setField(order, "status", Order.Status.PENDING);
        ReflectionTestUtils.setField(order, "orderMenus", List.of());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ReviewRequestDto dto = new ReviewRequestDto("개맛있네", 5);

        assertThrows(InvalidRequestException.class,
            () -> reviewService.createReview(user, orderId, dto));
    }

    @Test
    void createReview_ShouldThrowForbiddenException_해당_주문을_실행한_사람이_아닐때() {
        AuthUser user = new AuthUser(1L, "testUser", UserRole.USER);
        Long orderId = 1L;
        Order order = new Order();
        ReflectionTestUtils.setField(order, "user", new User(2L, "anotherUser"));
        ReflectionTestUtils.setField(order, "status", Order.Status.COMPLETED);
        ReflectionTestUtils.setField(order, "orderMenus", List.of());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ReviewRequestDto dto = new ReviewRequestDto("개맛있네", 5);

        assertThrows(ForbiddenException.class,
            () -> reviewService.createReview(user, orderId, dto));
    }
}
