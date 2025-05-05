package in.gppalanpur.portal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEvaluation {
    
    @Builder.Default
    private Boolean completed = false;
    
    private Double score;
    
    @Column(columnDefinition = "text")
    private String feedback;
    
    // Instead of using a ManyToOne relationship, we'll store just the ID
    // This avoids the complex mapping issues with embedded entities
    private Long juryId;
    
    private LocalDateTime evaluatedAt;
}