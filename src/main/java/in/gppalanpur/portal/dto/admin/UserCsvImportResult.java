package in.gppalanpur.portal.dto.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCsvImportResult {
    
    private Integer successful;
    private Integer failed;
    private String summary;
    private List<UserResponse> users;
    private List<String> errors;
}