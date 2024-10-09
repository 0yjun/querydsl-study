package study.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import study.querydsl.entities.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entities.QMember.member;
import static study.querydsl.entities.QTeam.team;

@Repository
public class MemberJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberJpaRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory;
    }


    public void save(Member member){
        em.persist(member);
    }

    public Optional<Member> findById(Long id){
        Member findMember = em.find(Member.class,id);
        return Optional.ofNullable(findMember);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findAll_querydsl(){
        return queryFactory
                .selectFrom(member)
                .fetch();
    }


    public List<Member> findByUsername(String username){
        return em.createQuery("select m from Member m where m.username =:username", Member.class)
                .setParameter("username",username)
                .getResultList();
    }

    public List<Member> findByUsername_querydsl(String username){
        return queryFactory
                .selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }

    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition){
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username
                        , member.age
                        , team.id.as("teamId")
                        , team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .fetch();
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
}
