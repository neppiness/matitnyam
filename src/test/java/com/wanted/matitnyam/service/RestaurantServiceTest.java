package com.wanted.matitnyam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.wanted.matitnyam.domain.Restaurant;
import com.wanted.matitnyam.dto.RegionRequest;
import com.wanted.matitnyam.dto.RestaurantDetailResponse;
import com.wanted.matitnyam.dto.RestaurantResponse;
import com.wanted.matitnyam.dto.ReviewShortResponse;
import com.wanted.matitnyam.repository.RestaurantRepository;
import java.io.IOException;
import java.util.List;
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
class RestaurantServiceTest {

    private final static ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @DisplayName("맛집 정보 업로드 테스트")
    @Test
    void uploadTest() throws JsonProcessingException {
        long restaurantId = 1L;
        Optional<Restaurant> mayBeFoundRestaurant = restaurantRepository.findById(restaurantId);
        assert mayBeFoundRestaurant.isPresent();
        Restaurant foundRestaurant = mayBeFoundRestaurant.get();

        String isClose = "폐업";
        Restaurant changedRestaurant = Restaurant.builder()
                .city(foundRestaurant.getCity())
                .name(foundRestaurant.getName())
                .closeOrOpen(isClose)
                .typeOfFoods(foundRestaurant.getTypeOfFoods())
                .addressAsRoadName(foundRestaurant.getAddressAsRoadName())
                .addressAsLocationName(foundRestaurant.getAddressAsLocationName())
                .latitude(foundRestaurant.getLatitude())
                .longitude(foundRestaurant.getLongitude())
                .build();

        Restaurant returnedRestaurant = restaurantService.upload(changedRestaurant);
        String returnedRestaurantAsString = objectWriter.writeValueAsString(returnedRestaurant);
        System.out.println(returnedRestaurantAsString);

        Assertions
                .assertThat(returnedRestaurant.getSeq())
                .isEqualTo(restaurantId);
    }

    @DisplayName("시/도, 시군구 정보로 맛집 목록 조회 테스트")
    @Test
    void regionNameBasedSearchTest() throws IOException {
        String dosi = "경기";
        String sgg = "용인시";
        RegionRequest regionRequest = RegionRequest.builder()
                .dosi(dosi)
                .sgg(sgg)
                .build();
        List<RestaurantResponse> returnedList = restaurantService.regionNameBasedSearch(regionRequest);
        for (RestaurantResponse restaurantResponse : returnedList) {
            String restaurantDtoAsString = objectWriter.writeValueAsString(restaurantResponse);
            System.out.println(restaurantDtoAsString);
        }
    }

    @DisplayName("맛집 상세 정보 조회 테스트")
    @Test
    void searchTest() throws JsonProcessingException {
        RestaurantDetailResponse restaurantDetailResponse = restaurantService.getDetailById(1L);
        String valueAsString = objectWriter.writeValueAsString(restaurantDetailResponse);
        System.out.println(valueAsString);
    }

    @DisplayName("리뷰 작성 후 맛집 ID를 통한 리뷰 조회 기능 테스트")
    @Test
    void getReviewsRestaurantByIdTest() throws JsonProcessingException {
        long restaurantId = 1L;
        List<ReviewShortResponse> reviewShortResponseList = restaurantService.getReviewsById(restaurantId);
        for (ReviewShortResponse reviewShortResponse : reviewShortResponseList) {
            String reviewDtoAsString = objectWriter.writeValueAsString(reviewShortResponse);
            System.out.println(reviewDtoAsString);
        }
    }

}