package in.gppalanpur.portal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jury_id")
    private User jury;
    
    private LocalDateTime evaluatedAt;
}