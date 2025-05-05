package in.gppalanpur.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination information for API responses to match the React frontend expectations.
 * 
 * @param <T> The type of data being paginated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {
    private int page;
    private int limit;
    private long total;
    private int totalPages;
}
