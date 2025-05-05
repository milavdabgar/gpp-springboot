package in.gppalanpur.portal.dto.location;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for location import results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationImportResult {
    private int totalProcessed;
    private int successCount;
    private int errorCount;
    private List<String> errors;
}
