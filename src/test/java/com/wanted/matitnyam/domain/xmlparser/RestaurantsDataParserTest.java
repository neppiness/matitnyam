package com.wanted.matitnyam.domain.xmlparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RestaurantsDataParserTest {

    private static final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    private RestaurantsDataParser restaurantsDataParser;

    @DisplayName("중식 음식점 파싱 테스트")
    @Test
    void chineseRestaurantDataParseTest() throws IOException, JAXBException {
        String testFilePath = "test/chinese-restaurant-test.xml";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(testFilePath);
        RestaurantsData restaurantsData = restaurantsDataParser.parse(ChineseRestaurants.class, inputStream);
        String restaurantsDataAsString = objectWriter.writeValueAsString(restaurantsData);
        System.out.println(restaurantsDataAsString);
    }

    @DisplayName("일식 음식점 파싱 테스트")
    @Test
    void japaneseRestaurantDataParseTest() throws IOException, JAXBException {
        String testFilePath = "test/japanese-restaurant-test.xml";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(testFilePath);
        RestaurantsData restaurantsData = restaurantsDataParser.parse(JapaneseRestaurants.class, inputStream);
        String restaurantsDataAsString = objectWriter.writeValueAsString(restaurantsData);
        System.out.println(restaurantsDataAsString);
    }

    @DisplayName("카페 파싱 테스트")
    @Test
    void cafesDataParseTest() throws IOException, JAXBException {
        String testFilePath = "test/cafe-test.xml";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(testFilePath);
        RestaurantsData restaurantsData = restaurantsDataParser.parse(Cafes.class, inputStream);
        String restaurantsDataAsString = objectWriter.writeValueAsString(restaurantsData);
        System.out.println(restaurantsDataAsString);
    }

}