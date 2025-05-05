package in.gppalanpur.portal.dto.location;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Location entity with camelCase property names to match React frontend expectations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class LocationResponse {
    private Long id;
    private String name;
    private String description;
    private String section;
    private String building;
    private String floor;
    private String room;
    private Integer capacity;
    private Boolean isActive;
    
    // Department info
    private Long departmentId;
    private String departmentName;
    
    // Project info
    private Long projectId;
    private String projectName;
    private String projectTeamName;
    
    // Event info
    private Long eventId;
    private String eventName;
    
    // Metadata
    private Long createdById;
    private String createdByName;
    private Long updatedById;
    private String updatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
