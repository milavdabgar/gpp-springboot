package in.gppalanpur.portal.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultAnalysisResponse {
    
    private String branchName;
    private Integer semester;
    private Double averageSpi;
    private Integer passCount;
    private Integer totalCount;
    private Double passPercentage;
}
