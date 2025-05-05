package in.gppalanpur.portal.dto.result;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultBatchResponse {
    
    private String batchId;
    private Integer count;
    private LocalDateTime uploadedAt;
}
