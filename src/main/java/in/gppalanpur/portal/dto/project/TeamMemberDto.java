package in.gppalanpur.portal.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for team member information with camelCase property names to match React frontend expectations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TeamMemberDto {
    private Long id;
    private String name;
    private String email;
    private String enrollmentNumber;
    private String phone;
    private Boolean isLeader;
    private String departmentName;
    private Integer semester;
}
