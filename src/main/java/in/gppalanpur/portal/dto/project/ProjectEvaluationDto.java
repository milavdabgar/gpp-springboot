package in.gppalanpur.portal.dto.project;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for project evaluation with camelCase property names to match React frontend expectations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ProjectEvaluationDto {
    private Boolean completed;
    private Double score;
    private String feedback;
    private Long juryId;
    private String juryName;
    private LocalDateTime evaluatedAt;
}
