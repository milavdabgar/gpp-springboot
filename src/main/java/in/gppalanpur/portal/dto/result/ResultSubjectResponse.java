package in.gppalanpur.portal.dto.result;

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
public class ResultSubjectResponse {
    
    private Long id;
    private String code;
    private String name;
    private Double credits;
    private String grade;
    private Boolean isBacklog;
    private String theoryEseGrade;     // External (E) - 70 marks
    private String theoryPaGrade;      // Mid-term/PA (M) - 30 marks
    private String theoryTotalGrade;   // Theory Total (E+M = 100 marks)
    private String practicalPaGrade;   // Internal/PA (I) - 20 marks
    private String practicalVivaGrade; // End Term Viva (V) - 30 marks
    private String practicalTotalGrade; // Practical Total (I+V = 50 marks)
}
