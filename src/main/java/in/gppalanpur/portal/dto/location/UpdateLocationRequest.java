package in.gppalanpur.portal.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing location.
 * Uses JsonProperty annotations to handle camelCase from React frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationRequest {
    
    private String name;
    
    private String description;
    
    private String section;
    
    private String building;
    
    private String floor;
    
    private String room;
    
    private Integer capacity;
    
    @JsonProperty("departmentId")
    private Long departmentId;
    
    @JsonProperty("isActive")
    private Boolean isActive;
}
