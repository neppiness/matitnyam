package com.wanted.matitnyam.domain;

import com.wanted.matitnyam.dto.MemberResponse;
import com.wanted.matitnyam.dto.PrincipalDto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Member {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long seq;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private Double latitude;

    private Double longitude;

    @Builder
    public Member(final String name, final String password) {
        this.name = name;
        this.password = password;
        this.authority = Authority.USER;
    }

    public void setCoordinates(final Coordinates coordinates) {
        this.latitude = coordinates.latitudeInDegrees();
        this.longitude = coordinates.longitudeInDegrees();
    }

    public MemberResponse toResponse() {
        return MemberResponse.builder()
                .name(this.name)
                .authority(this.authority)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .build();
    }

    public PrincipalDto toPrincipalDto() {
        return PrincipalDto.builder()
                .name(this.name)
                .authority(this.authority)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .build();
    }

}
