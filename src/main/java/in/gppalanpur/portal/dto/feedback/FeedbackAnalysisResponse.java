package in.gppalanpur.portal.dto.feedback;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAnalysisResponse {
    
    private String category;
    private String name;
    
    @Builder.Default
    private Map<String, Double> scores = new HashMap<>();
    
    private Integer count;
    private Double averageScore;
}
