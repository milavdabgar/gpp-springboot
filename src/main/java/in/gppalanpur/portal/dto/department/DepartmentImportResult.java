package in.gppalanpur.portal.dto.department;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentImportResult {
    
    private Integer successful;
    private Integer failed;
    private String message;
    private List<DepartmentResponse> successful_imports;
    private List<FailedImport> failed_imports;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedImport {
        private Object department;
        private String error;
    }
}