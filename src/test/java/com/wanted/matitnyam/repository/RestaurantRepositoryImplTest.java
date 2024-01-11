package com.wanted.matitnyam.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.wanted.matitnyam.domain.Coordinates;
import com.wanted.matitnyam.domain.Restaurant;
import com.wanted.matitnyam.dto.RestaurantRequest;
import com.wanted.matitnyam.dto.RestaurantResponse;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@Sql(value = "classpath:test/reset.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:test/init.sql")
@DataJpaTest
class RestaurantRepositoryImplTest {

    private static final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    private RestaurantRepository restaurantRepository;

    @DisplayName("음식점 이름과 도로명 주소로 맛집을 찾는 메소드 테스트")
    @Test
    void findByNameAndAddressAsRoadNameTest() throws JsonProcessingException {
        String name = "삼국지";
        String addressAsRoadName = "경기도 용인시 기흥구 한보라2로14번길 3-7 (보라동)";
        double latitude = 37.2539499121;
        double longitude = 127.1119282508;

        Optional<Restaurant> mayBeFoundRestaurant = restaurantRepository.findByNameAndAddressAsRoadName(name,
                addressAsRoadName);
        Assertions
                .assertThat(mayBeFoundRestaurant.isPresent())
                .isTrue();

        Restaurant foundRestaurant = mayBeFoundRestaurant.get();
        String foundRestaurantAsString = objectWriter.writeValueAsString(foundRestaurant);
        System.out.println(foundRestaurantAsString);

        Assertions
                .assertThat(foundRestaurant.getName())
                .isEqualTo(name);
        Assertions
                .assertThat(foundRestaurant.getLatitude())
                .isEqualTo(latitude);
        Assertions
                .assertThat(foundRestaurant.getLongitude())
                .isEqualTo(longitude);
    }

    @DisplayName("맛집 요청 테스트를 통해 해당하는 모든 맛집을 리스트로 조회하는 메소드 테스트(정렬 기준: 거리순)")
    @Test
    void findAllRestaurantsByRequestTest1() throws JsonProcessingException {
        double latitude = 37.2040;
        double longitude = 127.07596008849987;
        double range = 6.5;

        Coordinates source = Coordinates.builder()
                .latitudeInDegrees(latitude)
                .longitudeInDegrees(longitude)
                .build();

        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        List<Restaurant> restaurantsWithinRange = allRestaurants.stream()
                .filter(restaurant -> {
                    Coordinates destination = Coordinates.builder()
                            .latitudeInDegrees(restaurant.getLatitude())
                            .longitudeInDegrees(restaurant.getLongitude())
                            .build();
                    double distance = Coordinates.calculateDistance(source, destination);
                    System.out.print("맛집 이름:" + restaurant.getName() + ", ");
                    System.out.println("거리: " + distance + " km");
                    return distance <= range;
                })
                .toList();

        RestaurantRequest restaurantRequest = RestaurantRequest.builder()
                .latitude(latitude)
                .longitude(longitude)
                .range(range)
                .build();
        List<RestaurantResponse> resultList = restaurantRepository.findAllRestaurantsByRequest(restaurantRequest);
        double previousDistance = 0;
        for (RestaurantResponse restaurantResponse : resultList) {
            String restaurantDtoInJsonString = objectWriter.writeValueAsString(restaurantResponse);
            System.out.println(restaurantDtoInJsonString);
            Assertions
                    .assertThat(restaurantResponse.distance())
                    .isGreaterThan(previousDistance);
            previousDistance = restaurantResponse.distance();
        }

        Assertions
                .assertThat(restaurantsWithinRange.size())
                .isEqualTo(resultList.size());
    }

    @DisplayName("맛집 요청 테스트를 통해 해당하는 모든 맛집을 리스트로 조회하는 메소드 테스트(정렬 기준: 평점순)")
    @Test
    void findAllRestaurantsByRequestTest2() throws JsonProcessingException {
        double latitude = 37.2040;
        double longitude = 127.07596008849987;
        double range = 6.5;

        Coordinates source = Coordinates.builder()
                .latitudeInDegrees(latitude)
                .longitudeInDegrees(longitude)
                .build();

        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        List<Restaurant> restaurantsWithinRange = allRestaurants.stream()
                .filter(restaurant -> {
                    Coordinates destination = Coordinates.builder()
                            .latitudeInDegrees(restaurant.getLatitude())
                            .longitudeInDegrees(restaurant.getLongitude())
                            .build();
                    double distance = Coordinates.calculateDistance(source, destination);
                    System.out.print("맛집 이름:" + restaurant.getName() + ", ");
                    System.out.println("거리: " + distance + " km");
                    return distance <= range;
                })
                .toList();

        RestaurantRequest restaurantRequest = RestaurantRequest.builder()
                .latitude(latitude)
                .longitude(longitude)
                .range(range)
                .sortType("평점순")
                .build();
        List<RestaurantResponse> resultList = restaurantRepository.findAllRestaurantsByRequest(restaurantRequest);
        double previousRating = 5.0;
        for (RestaurantResponse restaurantResponse : resultList) {
            String restaurantDtoInJsonString = objectWriter.writeValueAsString(restaurantResponse);
            System.out.println(restaurantDtoInJsonString);
            Assertions
                    .assertThat(restaurantResponse.rating())
                    .isLessThanOrEqualTo(previousRating);
            previousRating = restaurantResponse.rating();
        }

        Assertions
                .assertThat(restaurantsWithinRange.size())
                .isEqualTo(resultList.size());
    }

}
