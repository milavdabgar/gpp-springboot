package in.gppalanpur.portal.dto.feedback;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackImportResult {
    
    @Builder.Default
    private Integer totalRecords = 0;
    
    @Builder.Default
    private Integer successCount = 0;
    
    @Builder.Default
    private Integer errorCount = 0;
    
    @Builder.Default
    private List<String> errors = new ArrayList<>();
    
    private String batchId;
}
