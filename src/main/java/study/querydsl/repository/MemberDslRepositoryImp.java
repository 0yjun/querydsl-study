package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import study.querydsl.entities.MemberSearchCondition;
import study.querydsl.entities.MemberTeamDto;


import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entities.QMember.member;
import static study.querydsl.entities.QTeam.team;

public class MemberDslRepositoryImp implements MemberDslRepository{

    private final JPAQueryFactory queryFactory;

    public MemberDslRepositoryImp(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName) ? member.username.eq(teamName): null;
    }

    private BooleanExpression usernameEq(String teamName) {
        return hasText(teamName) ? member.username.eq(teamName): null;
    }

    private BooleanExpression ageGoe(Integer age) {
        return age !=null ? member.age.goe(age): null;
    }

    private BooleanExpression ageLoe(Integer age) {
        return age!=null ? member.age.loe(age): null;
    }


    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition){
        return queryFactory
                .select(new study.querydsl.dto.QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username
                        , member.age
                        , team.id.as("teamId")
                        , team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe())
                ).fetch();
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> results = queryFactory
                .select(new study.querydsl.dto.QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username
                        , member.age
                        , team.id.as("teamId")
                        , team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<MemberTeamDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> results = queryFactory
                .select(new study.querydsl.dto.QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username
                        , member.age
                        , team.id.as("teamId")
                        , team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe())
                )
                .fetchCount();
        return new PageImpl<>(results, pageable, total);
    }


}
