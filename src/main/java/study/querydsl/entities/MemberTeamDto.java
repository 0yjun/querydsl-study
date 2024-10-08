package study.querydsl.entities;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Member}
 */
@Data
public class MemberTeamDto  {
    Long id;
    String username;
    int age;
    Long teamId;
    String teamName;


    @QueryProjection
    public MemberTeamDto(Long id, String username, int age, Long teamId, String teamName) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}