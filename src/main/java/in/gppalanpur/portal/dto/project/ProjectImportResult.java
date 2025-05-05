package in.gppalanpur.portal.dto.project;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for project import results.
 * Uses JsonProperty annotations to handle camelCase from React frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectImportResult {
    @JsonProperty("totalProcessed")
    private int totalProcessed;
    
    @JsonProperty("successCount")
    private int successCount;
    
    @JsonProperty("failureCount")
    private int failureCount;
    
    @JsonProperty("errors")
    private List<String> errors;
}
