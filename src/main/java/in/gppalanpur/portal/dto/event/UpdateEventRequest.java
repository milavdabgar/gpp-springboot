package in.gppalanpur.portal.dto.event;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing event.
 * Uses JsonProperty annotations to handle camelCase from React frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {
    
    private String name;
    
    private String description;
    
    @JsonProperty("startDate")
    private LocalDate startDate;
    
    @JsonProperty("endDate")
    private LocalDate endDate;
    
    @JsonProperty("registrationStartDate")
    private LocalDate registrationStartDate;
    
    @JsonProperty("registrationEndDate")
    private LocalDate registrationEndDate;
    
    @JsonProperty("resultsPublished")
    private Boolean resultsPublished;
    
    @JsonProperty("isActive")
    private Boolean isActive;
}
