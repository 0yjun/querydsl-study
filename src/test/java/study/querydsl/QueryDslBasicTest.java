package study.querydsl;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entities.Member;
import study.querydsl.entities.QMember;
import study.querydsl.entities.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entities.QMember.member;

@SpringBootTest
@Transactional
class QueryDslBasicTest {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;


    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10, teamA);
        Member member2 = new Member("member2",10, teamA);
        Member member3 = new Member("member3",10, teamA);
        Member member4 = new Member("member4",10, teamA);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
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
}
