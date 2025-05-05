package in.gppalanpur.portal.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new location.
 * Uses JsonProperty annotations to handle camelCase from React frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLocationRequest {
    
    @NotBlank(message = "Location name is required")
    private String name;
    
    private String description;
    
    private String section;
    
    private String building;
    
    private String floor;
    
    private String room;
    
    private Integer capacity;
    
    private Integer position;
    
    @NotNull(message = "Department ID is required")
    @JsonProperty("departmentId")
    private Long departmentId;
    
    @JsonProperty("eventId")
    private Long eventId;
    
    @JsonProperty("isActive")
    private Boolean isActive;
}
