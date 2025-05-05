package in.gppalanpur.portal.dto.feedback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAnalysisResult {
    
    @Builder.Default
    private List<FeedbackAnalysisResponse> subjectScores = new ArrayList<>();
    
    @Builder.Default
    private List<FeedbackAnalysisResponse> facultyScores = new ArrayList<>();
    
    @Builder.Default
    private List<FeedbackAnalysisResponse> semesterScores = new ArrayList<>();
    
    @Builder.Default
    private List<FeedbackAnalysisResponse> branchScores = new ArrayList<>();
    
    @Builder.Default
    private List<FeedbackAnalysisResponse> termYearScores = new ArrayList<>();
    
    @Builder.Default
    private Map<String, Map<String, Double>> correlationMatrix = new HashMap<>();
}
