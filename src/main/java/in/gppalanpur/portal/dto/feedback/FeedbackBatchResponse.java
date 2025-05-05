package in.gppalanpur.portal.dto.feedback;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackBatchResponse {
    
    private String batchId;
    private Integer count;
    private LocalDateTime uploadedAt;
}
