package com.wanted.matitnyam.domain.xmlparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.xml.bind.JAXBException;
import java.io.InputStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RestaurantsDataTest {

    private static final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    private RestaurantsDataParser restaurantsDataParser;

    @DisplayName("xml 전처리 테스트: 시 정보가 없는 음식점은 제외된다.")
    @Test
    void preprocessTest() throws JsonProcessingException, JAXBException {
        String testFilePath = "test/chinese-restaurant-test.xml";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(testFilePath);
        RestaurantsData restaurantsData = restaurantsDataParser
                .parse(ChineseRestaurants.class, inputStream);
        String restaurantsDataInJson = objectWriter.writeValueAsString(restaurantsData);
        System.out.println(restaurantsDataInJson);
        System.out.println();

        restaurantsData = restaurantsData.preprocess();
        String processedDataAsString = objectWriter.writeValueAsString(restaurantsData);
        System.out.println(processedDataAsString);

        Assertions
                .assertThat(restaurantsData.getRestaurants().size())
                .isEqualTo(2);
    }

}