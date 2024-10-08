package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entities.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberJpaRepositoryTest {

    EntityManager em;

    MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest(){
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member result1 = memberJpaRepository.findById(member.getId()).get();

        Assertions.assertThat(member).isEqualTo(result1);

        List<Member> result2 = memberJpaRepository.findByUsername("member1");

        Assertions.assertThat(member).isEqualTo(result2.get(0));
    }

    @Test
    public void basicQueryDslTest(){
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        List<Member> result1 = memberJpaRepository.findAll_querydsl();

        Assertions.assertThat(member).isEqualTo(result1);

        List<Member> result2 = memberJpaRepository.findByUsername_querydsl("member1");

        Assertions.assertThat(member).isEqualTo(result2.get(0));
    }

}