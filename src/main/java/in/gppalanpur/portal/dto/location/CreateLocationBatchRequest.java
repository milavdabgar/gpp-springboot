package in.gppalanpur.portal.dto.location;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for batch location creation requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLocationBatchRequest {
    
    @NotEmpty(message = "Locations list cannot be empty")
    private List<@Valid CreateLocationRequest> locations;
}
