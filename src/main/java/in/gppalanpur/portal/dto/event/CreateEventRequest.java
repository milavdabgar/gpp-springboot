package in.gppalanpur.portal.dto.event;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new event.
 * Uses JsonProperty annotations to handle camelCase from React frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    
    @NotBlank(message = "Event name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Start date is required")
    @JsonProperty("startDate")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    @JsonProperty("endDate")
    private LocalDate endDate;
    
    @JsonProperty("registrationStartDate")
    private LocalDate registrationStartDate;
    
    @JsonProperty("registrationEndDate")
    private LocalDate registrationEndDate;
    
    @JsonProperty("isActive")
    private Boolean isActive;
}
