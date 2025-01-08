package com.example.outsourcing.review;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.review.service.ReviewService;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeleteReviewTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    void deleteReview_ShouldThrowInvalidRequestException_해당_리뷰가_없을때() {
        AuthUser user = new AuthUser(1L, "testUser", UserRole.USER);
        Long reviewId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class,
            () -> reviewService.deleteReview(user, reviewId));
    }

    @Test
    void deleteReview_ShouldThrowForbiddenException_리뷰_작성자가_아닐때() {
        AuthUser user = new AuthUser(2L, "notOwner", UserRole.USER);
        Long reviewId = 1L;
        Review review = new Review(reviewId, new User(1L, "ownerUser"), "Test content", 5);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        assertThrows(ForbiddenException.class, () -> reviewService.deleteReview(user, reviewId));
    }

}
