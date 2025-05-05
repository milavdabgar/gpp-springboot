package in.gppalanpur.portal.dto.project;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gppalanpur.portal.entity.Project.Status;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing project.
 * Uses JsonProperty annotations to handle camelCase from React frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectRequest {
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    
    private String category;
    
    @JsonProperty("abstract")
    @Size(min = 10, message = "Abstract must be at least 10 characters")
    private String abstract_;
    
    @JsonProperty("departmentId")
    private Long departmentId;
    
    @JsonProperty("teamId")
    private Long teamId;
    
    @JsonProperty("eventId")
    private Long eventId;
    
    @JsonProperty("locationId")
    private Long locationId;
    
    private Status status;
    
    @JsonProperty("requirements")
    private Map<String, Boolean> requirements;
    
    @JsonProperty("guideName")
    private String guideName;
    
    @JsonProperty("guideEmail")
    private String guideEmail;
    
    @JsonProperty("guidePhone")
    private String guidePhone;
}
