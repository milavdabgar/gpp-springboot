package in.gppalanpur.portal.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria {
    
    private String search;
    private String role;
    private Long departmentId;
    private String sortBy;
    private String sortOrder;
}