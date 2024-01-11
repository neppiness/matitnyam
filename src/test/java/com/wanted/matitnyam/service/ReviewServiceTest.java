package com.wanted.matitnyam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.wanted.matitnyam.domain.Restaurant;
import com.wanted.matitnyam.domain.Review;
import com.wanted.matitnyam.dto.ReviewRequest;
import com.wanted.matitnyam.dto.ReviewResponse;
import com.wanted.matitnyam.repository.RestaurantRepository;
import com.wanted.matitnyam.repository.ReviewRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Sql(value = "classpath:test/reset.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:test/init.sql")
@SpringBootTest
class ReviewServiceTest {

    private static final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewService reviewService;

    @DisplayName("create 메소드 테스트: 평점 갱신 로직 확인")
    @Test
    void createTest() throws JsonProcessingException {
        Long restaurantId = 1L;
        String username = "neppiness";
        int rating = 5;
        String opinion = "최고에요!";

        Optional<Restaurant> mayBeFoundRestaurant = restaurantRepository.findById(restaurantId);
        assert mayBeFoundRestaurant.isPresent();
        Restaurant foundRestaurant = mayBeFoundRestaurant.get();

        long numberOfReviewsBeforeUpdate = foundRestaurant.getNumberOfReviews();
        long totalRatingsBeforeUpdate = foundRestaurant.getTotalRatings();

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .reviewId(null)
                .restaurantId(restaurantId)
                .rating(rating)
                .opinion(opinion)
                .build();
        reviewService.create(reviewRequest, username);

        Optional<Restaurant> mayBeUpdatedRestaurant = restaurantRepository.findById(restaurantId);
        assert mayBeUpdatedRestaurant.isPresent();
        Restaurant updatedRestaurant = mayBeUpdatedRestaurant.get();

        String updatedRestaurantAsString = objectWriter.writeValueAsString(updatedRestaurant);
        System.out.println("리뷰 생성 후 갱신된 맛집 조회 결과:");
        System.out.println(updatedRestaurantAsString);

        long expectedNumberOfReviews = numberOfReviewsBeforeUpdate + 1;
        long expectedTotalRatings = totalRatingsBeforeUpdate + rating;
        double expectedRating = (double) expectedTotalRatings / expectedNumberOfReviews;

        Assertions
                .assertThat(updatedRestaurant.getNumberOfReviews())
                .isEqualTo(expectedNumberOfReviews);
        Assertions
                .assertThat(updatedRestaurant.getTotalRatings())
                .isEqualTo(expectedTotalRatings);
        Assertions
                .assertThat(updatedRestaurant.getRating())
                .isEqualTo(expectedRating);
    }

    @DisplayName("update 메소드 테스트: 평점 갱신 로직 확인")
    @Test
    void updateTest() throws JsonProcessingException {
        Long restaurantId = 1L;
        String username = "neppiness";
        int originalRating = 5;
        String originalOpinion = "최고에요!";

        ReviewRequest reviewRequestToCreate = ReviewRequest.builder()
                .reviewId(null)
                .restaurantId(restaurantId)
                .rating(originalRating)
                .opinion(originalOpinion)
                .build();
        ReviewResponse createdReviewResponse = reviewService.create(reviewRequestToCreate, username);

        Optional<Restaurant> mayBeFoundRestaurant = restaurantRepository.findById(restaurantId);
        assert mayBeFoundRestaurant.isPresent();
        Restaurant foundRestaurant = mayBeFoundRestaurant.get();

        String foundRestaurantAsString = objectWriter.writeValueAsString(foundRestaurant);
        System.out.println("\n리뷰 작성 후 갱신된 맛집 조회 결과:");
        System.out.println(foundRestaurantAsString);

        long numberOfReviewsBeforeModification = foundRestaurant.getNumberOfReviews();
        long totalRatingsBeforeModification = foundRestaurant.getTotalRatings();

        int modifiedRating = 4;
        String modifiedOpinion = "일행들은 엄청 좋아했는데 저는 음식이 조금 잘 안 맞았어요.";
        ReviewRequest reviewRequestToModify = ReviewRequest.builder()
                .reviewId(createdReviewResponse.seq())
                .restaurantId(restaurantId)
                .rating(modifiedRating)
                .opinion(modifiedOpinion)
                .build();
        reviewService.update(reviewRequestToModify, username);

        Optional<Restaurant> mayBeModifiedRestaurant = restaurantRepository.findById(restaurantId);
        assert mayBeModifiedRestaurant.isPresent();
        Restaurant modifiedRestaurant = mayBeModifiedRestaurant.get();
        String modifiedRestaurantAsString = objectWriter.writeValueAsString(modifiedRestaurant);
        System.out.println("\n리뷰 수정 후 갱신된 맛집 조회 결과:");
        System.out.println(modifiedRestaurantAsString);

        long expectedTotalRatings = totalRatingsBeforeModification - originalRating + modifiedRating;
        double expectedRating = (double) expectedTotalRatings / numberOfReviewsBeforeModification;

        Assertions
                .assertThat(modifiedRestaurant.getNumberOfReviews())
                .isEqualTo(numberOfReviewsBeforeModification);
        Assertions
                .assertThat(modifiedRestaurant.getTotalRatings())
                .isEqualTo(expectedTotalRatings);
        Assertions
                .assertThat(modifiedRestaurant.getRating())
                .isEqualTo(expectedRating);
    }

    @DisplayName("delete 메소드 테스트: 평점 갱신 로직 및 데이터 변화 확인")
    @Test
    void deleteTest() throws JsonProcessingException {
        Long restaurantId = 1L;
        String username = "neppiness";
        int rating = 5;
        String opinion = "최고에요!";

        Optional<Restaurant> mayBeFoundRestaurantBeforeCreateAndDelete = restaurantRepository.findById(restaurantId);
        assert mayBeFoundRestaurantBeforeCreateAndDelete.isPresent();
        Restaurant foundRestaurantBeforeCreateAndDelete = mayBeFoundRestaurantBeforeCreateAndDelete.get();

        ReviewRequest reviewRequestToCreate = ReviewRequest.builder()
                .reviewId(null)
                .restaurantId(restaurantId)
                .rating(rating)
                .opinion(opinion)
                .build();
        ReviewResponse createdReviewResponse = reviewService.create(reviewRequestToCreate, username);

        Optional<Restaurant> mayBeFoundRestaurantBeforeDelete = restaurantRepository.findById(restaurantId);
        assert mayBeFoundRestaurantBeforeDelete.isPresent();
        Restaurant foundRestaurantBeforeDelete = mayBeFoundRestaurantBeforeDelete.get();
        String foundRestaurantBeforeDeleteAsString = objectWriter.writeValueAsString(
                foundRestaurantBeforeDelete);
        System.out.println("\n리뷰 작성 후 맛집 조회 결과:");
        System.out.println(foundRestaurantBeforeDeleteAsString);

        reviewService.delete(createdReviewResponse.seq(), username);

        Optional<Restaurant> mayBeFoundRestaurantAfterCreateAndDelete = restaurantRepository.findById(restaurantId);
        assert mayBeFoundRestaurantAfterCreateAndDelete.isPresent();
        Restaurant foundRestaurantAfterCreateAndDelete = mayBeFoundRestaurantBeforeCreateAndDelete.get();

        String foundRestaurantAfterCreateAndDeleteAsString = objectWriter.writeValueAsString(
                foundRestaurantAfterCreateAndDelete);
        System.out.println("\n리뷰 삭제 후 맛집 조회 결과:");
        System.out.println(foundRestaurantAfterCreateAndDeleteAsString);

        long numberOfReviewsBeforeModification = foundRestaurantBeforeCreateAndDelete.getNumberOfReviews();
        long totalRatingsBeforeModification = foundRestaurantBeforeCreateAndDelete.getTotalRatings();
        double ratingBeforeModification = foundRestaurantBeforeCreateAndDelete.getRating();

        long numberOfReviewsAfterModification = foundRestaurantAfterCreateAndDelete.getNumberOfReviews();
        long totalRatingsAfterModification = foundRestaurantAfterCreateAndDelete.getTotalRatings();
        double ratingAfterModification = foundRestaurantAfterCreateAndDelete.getRating();

        Assertions
                .assertThat(numberOfReviewsAfterModification)
                .isEqualTo(numberOfReviewsBeforeModification);
        Assertions
                .assertThat(totalRatingsAfterModification)
                .isEqualTo(totalRatingsBeforeModification);
        Assertions
                .assertThat(ratingAfterModification)
                .isEqualTo(ratingBeforeModification);

        Optional<Review> mayBeFoundDeletedReview = reviewRepository.findById(createdReviewResponse.seq());
        Assertions
                .assertThat(mayBeFoundDeletedReview.isEmpty())
                .isTrue();
    }

}