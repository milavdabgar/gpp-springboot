package in.gppalanpur.portal.dto.team;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for team member operations.
 * Uses JsonProperty annotations to handle camelCase from React frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberRequest {
    
    @JsonProperty("userId")
    private Long userId;
    
    private String role;
}
