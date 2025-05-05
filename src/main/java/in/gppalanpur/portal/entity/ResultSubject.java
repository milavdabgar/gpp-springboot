package in.gppalanpur.portal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "result_subjects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultSubject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private Result result;
    
    @NotBlank
    private String code;
    
    @NotBlank
    private String name;
    
    @NotNull
    private Double credits;
    
    private String grade;
    
    @Builder.Default
    private Boolean isBacklog = false;
    
    private String theoryEseGrade;     // External (E) - 70 marks
    private String theoryPaGrade;      // Mid-term/PA (M) - 30 marks
    private String theoryTotalGrade;   // Theory Total (E+M = 100 marks)
    private String practicalPaGrade;   // Internal/PA (I) - 20 marks
    private String practicalVivaGrade; // End Term Viva (V) - 30 marks
    private String practicalTotalGrade; // Practical Total (I+V = 50 marks)
}