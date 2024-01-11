package com.wanted.matitnyam.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wanted.matitnyam.dto.ReviewResponse;
import com.wanted.matitnyam.dto.ReviewShortResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Review {

    private static final String ellipsis = "...";

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    Long seq;

    @ManyToOne
    @JoinColumn(referencedColumnName = "seq")
    Member member;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "seq")
    Restaurant restaurant;

    Integer rating;

    String opinion;

    @Builder
    public Review(final Long seq, final Member member, final Restaurant restaurant, final Integer rating,
                  final String opinion) {
        this.seq = seq;
        this.member = member;
        this.restaurant = restaurant;
        this.rating = rating;
        this.opinion = opinion;
    }

    public ReviewResponse toResponse() {
        return ReviewResponse.builder()
                .seq(this.seq)
                .memberResponse(this.member.toResponse())
                .restaurantDetailResponse(this.restaurant.toDetailResponse())
                .rating(this.rating)
                .opinion(this.opinion)
                .build();
    }

    public ReviewShortResponse toShortResponse() {
        String shortOpinion = getShortOpinion();
        return ReviewShortResponse.builder()
                .username(this.member.getName())
                .rating(this.rating)
                .opinionInShort(shortOpinion)
                .build();
    }

    private String getShortOpinion() {
        if (this.opinion.length() < ReviewShortResponse.lengthLimit) {
            return opinion;
        }
        return this.opinion.substring(0, ReviewShortResponse.lengthLimit) + ellipsis;
    }

}