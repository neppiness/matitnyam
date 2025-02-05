package com.wanted.matitnyam.repository;

import com.wanted.matitnyam.domain.Member;
import com.wanted.matitnyam.dto.MemberDetailResponse;
import com.wanted.matitnyam.dto.MemberRequest;
import com.wanted.matitnyam.dto.PrincipalDto;
import java.util.Optional;

public interface MemberCustomRepository {

    boolean hasDuplicatedName(String username);

    Optional<PrincipalDto> findPrincipalByNameAndPassword(MemberRequest memberRequest);

    Optional<Member> findByUsername(String username);

    Optional<MemberDetailResponse> findDetail(String username);

}
