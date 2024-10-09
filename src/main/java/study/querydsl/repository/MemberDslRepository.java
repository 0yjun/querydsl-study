package study.querydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.entities.MemberSearchCondition;
import study.querydsl.entities.MemberTeamDto;

import java.util.List;

public interface MemberDslRepository {
    List<MemberTeamDto> search(MemberSearchCondition condition);
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);
    Page<MemberTeamDto> searchComplex(MemberSearchCondition condition, Pageable pageable);
}
