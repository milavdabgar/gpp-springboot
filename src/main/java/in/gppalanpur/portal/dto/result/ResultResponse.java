package in.gppalanpur.portal.dto.result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
public class ResultResponse {
    
    private Long id;
    private String stId;
    private String enrollmentNo;
    private String extype;
    private Integer examId;
    private String exam;
    private LocalDate declarationDate;
    private String academicYear;
    private Integer semester;
    private Double unitNo;
    private Double examNumber;
    private String name;
    private Integer instCode;
    private String instName;
    private String courseName;
    private Integer branchCode;
    private String branchName;
    private List<ResultSubjectResponse> subjects;
    private Double totalCredits;
    private Double earnedCredits;
    private Double spi;
    private Double cpi;
    private Double cgpa;
    private String result;
    private Integer trials;
    private String uploadBatch;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
