package com.wanted.matitnyam.domain.csvparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.wanted.matitnyam.domain.Region;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SggDataParserTest {

    private static final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    private SggDataParser sggDataParser;

    @DisplayName("csv 파싱 테스트")
    @Test
    void parseTest() throws IOException {
        List<Region> regionList = sggDataParser.parse();
        for (Region region : regionList) {
            String regionAsString = objectWriter.writeValueAsString(region);
            System.out.println(regionAsString);
        }
    }

}
