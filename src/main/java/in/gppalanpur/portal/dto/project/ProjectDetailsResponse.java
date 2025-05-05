package in.gppalanpur.portal.dto.project;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Extended response DTO for Project entity with additional details.
 * Uses camelCase property names to match React frontend expectations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class ProjectDetailsResponse extends ProjectResponse {
    private List<TeamMemberDto> teamMembers;
    private String teamLeaderName;
    private String teamLeaderEmail;
    private String departmentHodName;
    private String departmentHodEmail;
    private String eventDescription;
    private String eventStartDate;
    private String eventEndDate;
    private Boolean eventResultsPublished;
}
