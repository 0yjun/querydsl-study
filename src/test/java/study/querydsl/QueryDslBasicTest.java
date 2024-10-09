package study.querydsl;

import com.mysema.commons.lang.Assert;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.criteria.Expression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entities.*;
import study.querydsl.repository.MemberJpaRepository;

import java.util.List;

import static com.jayway.jsonpath.internal.Utils.isEmpty;
import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entities.QMember.*;
import static study.querydsl.entities.QTeam.*;

@SpringBootTest
@Transactional
class QueryDslBasicTest {
    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10, teamA);
        Member member2 = new Member("member2",20, teamA);
        Member member3 = new Member("member3",30, teamA);
        Member member4 = new Member("member4",40, teamA);
        Member member5 = new Member("member5",10, teamB);
        Member member6 = new Member("member6",20, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);
        em.persist(member6);
    }

    @Test
    public void startJPQL(){
        Member findMember = em.createQuery("select m from Member m where m.username =:username",Member.class)
                .setParameter("username","member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl(){

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member2"))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member2");
    }

    @Test
    public void search(){
        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam(){
        Member findMember = queryFactory.selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resultFetch(){
        JPAQuery<Member> selected = queryFactory.selectFrom(member);
//        List<Member> fetch = selected.fetch();
//
//        Member fetchOne = selected.fetchOne();
//
//        Member fetchFirst = selected.fetchFirst();

        QueryResults<Member> result = selected.fetchResults();

        result.getTotal();
        List<Member> content = result.getResults();
    }

    @Test
    public void sort(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();
        for (Member member: result) {
            System.out.println("reslut " + member);
        }
    }

    @Test
    public void paging(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.age.desc())
                .offset(1)
                .limit(2)
                .fetch();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void aggregation(){
        List<Tuple> result = queryFactory
                .select(
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                        .from(member)
                                .fetch();
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    }

    @Test
    public void sqlFunctrion() throws Exception{
        //given
        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('replace',{0},{1},{2})"
                        ,member.username, "member", "M"))
                .from(member)
                .fetch();
        for (String str: result) {
            System.out.println(str);
        }
        assertThat(result.size()).isEqualTo(1);
        //when

        //then
    }
    @Test
    public void join() throws Exception {
        //given
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();
        //when
        assertThat(result)
                .extracting("username")
                .containsExactly("member1","member2");
        //then
    }

    @Test
    public void join_on_filtering() throws Exception {
        //given
        // select m, t from memver m left join m.team t on t.anme = 'teamA'
        List<Tuple> result = queryFactory
                .select(member,team)
                .from(member)
                .join(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();
        for (Tuple tuple : result){
            System.out.println("tuple "+tuple);
        }
        //when

        //then
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoinNo() throws Exception {
        //given
        //em.flush();
        //em.clear();
        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();
        System.out.println("findMember " +findMember);
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).isFalse();
    }

    @Test
    public void subQuery() throws Exception {

        QMember MemberSub = new QMember("memberSub");
        //given
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(MemberSub.age.max())
                                .from(MemberSub)
                ))
                .fetch();
        //when
assertThat(result).extracting("age").isEqualTo(40);
        //then
    }


    @Test
    public void subQueryGOE() throws Exception {

        QMember MemberSub = new QMember("memberSub");
        //given
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(MemberSub.age.avg())
                                .from(MemberSub)
                ))
                .fetch();
        //when
        assertThat(result).extracting("age").isEqualTo(40);
        //then
    }


   @Test
   public void selectSub() throws Exception {
       //given
       QMember memberSub = new QMember("memberSub");
       List<Tuple> result = queryFactory
               .select(
                       member.username
                       , select(memberSub.age.avg())
                               .from(memberSub)
               )
               .from(member)
               .fetch();
       for (Tuple tuple:result){
           System.out.println("tuple = "+tuple);
       }
       //when

       //then
   }

   @Test
   public void basicCasse() throws Exception {
       //given
       List<String> result = queryFactory
               .select(
                       new CaseBuilder()
                               .when(member.age.between(10,20)).then("10 20 살")
                               .when(member.age.between(21,30)).then("21 - 30 살")
                               .otherwise("기타")
               )
               .from(member)
               .fetch();
       for (String s : result){
           System.out.println("s = "+s);
       }

       //when

       //then
   }
   @Test
   public void constant() throws Exception {
       //given
       List<Tuple> result = queryFactory
               .select(member.username, Expressions.constant("A"))
               .from(member)
               .fetch();

       for (Tuple tuple : result){
           System.out.println("result = "+result);
       }
       //when

       //then
   }

   @Test
   public void projection() throws Exception {
       //given
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();
        for (Tuple tuple : result){
            String username = tuple.get(member.username);
        }

       //when

       //then
   }

   @Test
   public void findDtoByJPQL() throws Exception {
       //given
       List<MemberDto> result = em.createQuery(
               "select new study.querydsl.dto.MemberDto(m.username,m.age) from Member m"
               , MemberDto.class).getResultList();

       //when

       //then
   }

   @Test
   public void findDtoBySetter() throws Exception {
       //given
       List<MemberDto> result = queryFactory
               .select(Projections.bean(MemberDto.class
               ,member.username
               ,member.age))
               .from(member)
               .fetch();
       for (MemberDto memberDto : result){
           System.out.println(memberDto.toString());
       }
       //when

       //then
   }

    @Test
    public void findDtoByConstructor() throws Exception {
        //given
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class
                        ,member.username
                        ,member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result){
            System.out.println(memberDto.toString());
        }
        //when

        //then
    }

    @Test
    public void findUserDto() throws Exception {
        //given
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(Projections.constructor(UserDto.class
                        ,member.username.as("name")
                        , ExpressionUtils.as(
                                JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                                , "age")
                ))
                .from(member)
                .fetch();
        for (UserDto memberDto : result){
            System.out.println(memberDto.toString());
        }
        //when

        //then
    }

    @Test
    public void findDtoByQueryProjection() throws Exception {
        //given
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username,member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result){
            System.out.println(memberDto.toString());
        }
        //when

        //then
    }

    @Test
    public void dynamicQuery_BooleanBuilder() throws Exception {
        //given
        String usernameParam = "member1";
        Integer ageParam = null;
        List<Member> result = searchMember2(usernameParam,ageParam);
        assertThat(result.size()).isEqualTo(1);
        //when

        //then
    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();
        if(usernameCond!=null){
            builder.and(member.username.eq(usernameCond));
        }

        if(ageCond!=null){
            builder.and(member.age.eq(ageCond));
        }

        return queryFactory
                .select(member)
                .from(member)
                .where(builder)
                .fetch();
    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .select(member)
                .from(member)
                //.where(usernameEq(usernameCond), ageEq(ageCond))
                .fetch();
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }
    

    private BooleanExpression allEq(String usernameCond, Integer ageCond){
        return usernameEq2(usernameCond).and(ageEq(ageCond));
    }

    @Test
    public void sqlFunction() throws Exception {
        //given

        //when

        //then
    }
    private BooleanExpression usernameEq2(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }

    @Test
    public void jpaRepoTest (){
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(20);
        condition.setAgeLoe(40);
        List<MemberTeamDto> result = memberJpaRepository.search(condition);
        for (MemberTeamDto dto : result){
            System.out.println(dto.toString());
        }
    }
}
