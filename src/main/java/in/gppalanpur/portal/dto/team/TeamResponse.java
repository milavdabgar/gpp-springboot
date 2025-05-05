package in.gppalanpur.portal.dto.team;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import in.gppalanpur.portal.dto.project.TeamMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Team entity with camelCase property names to match React frontend expectations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TeamResponse {
    private Long id;
    private String name;
    private String description;
    
    // Department info
    private Long departmentId;
    private String departmentName;
    
    // Leader info
    private Long leaderId;
    private String leaderName;
    private String leaderEmail;
    
    // Members
    private List<TeamMemberDto> members;
    
    // Status
    private Boolean isActive;
    
    // Metadata
    private Long createdById;
    private String createdByName;
    private Long updatedById;
    private String updatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
