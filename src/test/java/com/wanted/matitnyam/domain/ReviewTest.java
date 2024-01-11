package com.wanted.matitnyam.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.wanted.matitnyam.dto.ReviewShortResponse;
import com.wanted.matitnyam.repository.MemberRepository;
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
class ReviewTest {

    private static final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("ReviewDto 변환 메소드 테스트")
    @Test
    void toDtoTest() throws JsonProcessingException {
        Optional<Member> mayBeFoundMember = memberRepository.findById(1L);
        assert mayBeFoundMember.isPresent();
        Member foundMember = mayBeFoundMember.get();

        int rating = 5;
        String opinion = "Dto의 문자열 길이는 30으로 제한됩니다. 길이가 30이 넘는 문자열의 경우 문자열 길이 30까지 부분 "
                + "문자열을 취해 생략(...)표시를 붙이는 방식으로 구현했습니다.";
        String shortOpinion = "Dto의 문자열 길이는 30으로 제한됩니다. 길이가 3...";
        //                                                               ^ 30번째 문자
        Review review = Review.builder()
                .member(foundMember)
                .restaurant(null)
                .rating(rating)
                .opinion(opinion)
                .build();
        ReviewShortResponse reviewShortResponse = review.toShortResponse();
        String reviewDtoAsString = objectWriter.writeValueAsString(reviewShortResponse);
        System.out.println(reviewDtoAsString);

        Assertions
                .assertThat(reviewShortResponse.opinionInShort())
                .isEqualTo(shortOpinion);
    }

}