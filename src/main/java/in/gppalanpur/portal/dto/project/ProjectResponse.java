package in.gppalanpur.portal.dto.project;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import in.gppalanpur.portal.entity.Project.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Project entity with camelCase property names to match React frontend expectations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ProjectResponse {
    private Long id;
    private String title;
    private String category;
    private String abstract_;
    private Status status;
    
    // Department info
    private Long departmentId;
    private String departmentName;
    
    // Team info
    private Long teamId;
    private String teamName;
    
    // Event info
    private Long eventId;
    private String eventName;
    
    // Location info
    private Long locationId;
    private String locationName;
    private String locationSection;
    
    // Requirements
    private Map<String, Boolean> requirements;
    
    // Other requirements (text)
    private String otherRequirements;
    
    // Guide info
    private String guideName;
    private String guideEmail;
    private String guidePhone;
    
    // Evaluation info
    private ProjectEvaluationDto deptEvaluation;
    private ProjectEvaluationDto centralEvaluation;
    
    // Metadata
    private Long createdById;
    private String createdByName;
    private Long updatedById;
    private String updatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
