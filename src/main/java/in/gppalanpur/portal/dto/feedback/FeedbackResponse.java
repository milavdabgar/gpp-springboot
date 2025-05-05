package in.gppalanpur.portal.dto.feedback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class FeedbackResponse {
    
    private Long id;
    private String year;
    private String term;
    private String branch;
    private Integer semester;
    private LocalDate termStart;
    private LocalDate termEnd;
    private String subjectCode;
    private String subjectName;
    private String facultyName;
    
    @Builder.Default
    private Map<String, Integer> ratings = new HashMap<>();
    
    private Long uploadedById;
    private String uploadedByName;
    private String batchId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
