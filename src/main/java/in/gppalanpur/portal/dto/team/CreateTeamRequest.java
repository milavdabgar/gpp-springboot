package in.gppalanpur.portal.dto.team;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new team.
 * Uses JsonProperty annotations to handle camelCase from React frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTeamRequest {
    
    @NotBlank(message = "Team name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Department ID is required")
    @JsonProperty("departmentId")
    private Long departmentId;
    
    @JsonProperty("leaderId")
    private Long leaderId;
    
    @JsonProperty("memberIds")
    private List<Long> memberIds;
    
    @JsonProperty("isActive")
    private Boolean isActive;
}
