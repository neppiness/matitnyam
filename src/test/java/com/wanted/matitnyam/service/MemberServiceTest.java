package com.wanted.matitnyam.service;

import com.wanted.matitnyam.domain.Coordinates;
import com.wanted.matitnyam.domain.Member;
import com.wanted.matitnyam.dto.MemberDetailResponse;
import com.wanted.matitnyam.dto.MemberRequest;
import com.wanted.matitnyam.dto.TokenResponse;
import com.wanted.matitnyam.repository.MemberRepository;
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
@SpringBootTest
@Sql(value = "classpath:test/reset.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:test/init.sql")
class MemberServiceTest {

    private static final String username = "kim-jeonghyun";

    private static final String password = "1234";

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("set 중복 요청 테스트")
    @Test
    void duplicatedMemberTest() {
        MemberRequest memberRequest = MemberRequest.builder()
                .name(username)
                .password(password)
                .build();

        Assertions
                .assertThatThrownBy(() -> memberService.set(memberRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("signIn 테스트")
    @Test
    void signInTest() {
        MemberRequest memberRequest = MemberRequest.builder()
                .name(username)
                .password(password)
                .build();

        TokenResponse response = memberService.signIn(memberRequest);
        System.out.println(response.token());
    }

    @DisplayName("update 테스트")
    @Test
    void updateTest() {
        Double latitude = 3.14;
        Double longitude = 1.592;

        Coordinates coordinates = Coordinates.builder()
                .latitudeInDegrees(latitude)
                .longitudeInDegrees(longitude)
                .build();
        memberService.updateCoordinates(coordinates, username);

        Optional<Member> mayBeFoundMember = memberRepository.findByUsername(username);
        Assertions
                .assertThat(mayBeFoundMember.isPresent())
                .isTrue();

        Member foundMember = mayBeFoundMember.get();
        Assertions
                .assertThat(foundMember.getLatitude())
                .isEqualTo(latitude);
        Assertions
                .assertThat(foundMember.getLongitude())
                .isEqualTo(longitude);
    }

    @DisplayName("getDetail 테스트")
    @Test
    void getDetailTest() {
        Double latitude = 3.14;
        Double longitude = 1.592;

        Coordinates coordinates = Coordinates.builder()
                .latitudeInDegrees(latitude)
                .longitudeInDegrees(longitude)
                .build();
        memberService.updateCoordinates(coordinates, username);

        Optional<MemberDetailResponse> mayBeFoundMemberDetails = memberRepository.findDetail(username);
        assert mayBeFoundMemberDetails.isPresent();
        MemberDetailResponse foundMemberDetailResponse = mayBeFoundMemberDetails.get();

        Assertions
                .assertThat(foundMemberDetailResponse.latitude())
                .isEqualTo(latitude);
        Assertions
                .assertThat(foundMemberDetailResponse.longitude())
                .isEqualTo(longitude);
    }

}