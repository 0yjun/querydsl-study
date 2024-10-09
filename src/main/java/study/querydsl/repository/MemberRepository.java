package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.querydsl.entities.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long>, MemberDslRepository {
    List<Member> findByUsername(String username);

}
