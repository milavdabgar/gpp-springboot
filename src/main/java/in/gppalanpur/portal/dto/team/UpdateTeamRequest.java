package in.gppalanpur.portal.dto.team;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing team.
 * Uses JsonProperty annotations to handle camelCase from React frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamRequest {
    
    private String name;
    
    private String description;
    
    @JsonProperty("departmentId")
    private Long departmentId;
    
    @JsonProperty("leaderId")
    private Long leaderId;
    
    @JsonProperty("memberIds")
    private List<Long> memberIds;
    
    @JsonProperty("isActive")
    private Boolean isActive;
}
