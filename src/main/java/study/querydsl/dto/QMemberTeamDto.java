package study.querydsl.dto;

import com.querydsl.core.types.ConstructorExpression;
import study.querydsl.entities.MemberTeamDto;

import javax.annotation.processing.Generated;

/**
 * study.querydsl.entities.QMemberTeamDto is a Querydsl Projection type for MemberTeamDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMemberTeamDto extends ConstructorExpression<study.querydsl.entities.MemberTeamDto> {

    private static final long serialVersionUID = -1509532497L;

    public QMemberTeamDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> username, com.querydsl.core.types.Expression<Integer> age, com.querydsl.core.types.Expression<Long> teamId, com.querydsl.core.types.Expression<String> teamName) {
        super(MemberTeamDto.class, new Class<?>[]{long.class, String.class, int.class, long.class, String.class}, id, username, age, teamId, teamName);
    }

}

