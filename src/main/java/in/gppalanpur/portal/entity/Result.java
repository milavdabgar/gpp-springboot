package in.gppalanpur.portal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "results", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"enrollment_no", "exam_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Result {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String stId;
    
    @NotBlank
    @Column(name = "enrollment_no")
    private String enrollmentNo;
    
    private String extype;
    
    @Column(name = "exam_id")
    private Integer examId;
    
    private String exam;
    
    private LocalDate declarationDate;
    
    private String academicYear;
    
    @NotNull
    private Integer semester;
    
    private Double unitNo;
    
    private Double examNumber;
    
    @NotBlank
    private String name;
    
    private Integer instCode;
    
    private String instName;
    
    private String courseName;
    
    private Integer branchCode;
    
    @NotBlank
    private String branchName;
    
    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ResultSubject> subjects = new ArrayList<>();
    
    private Double totalCredits;
    
    private Double earnedCredits;
    
    private Double spi;
    
    private Double cpi;
    
    private Double cgpa;
    
    private String result;
    
    @Builder.Default
    private Integer trials = 1;
    
    private String remark;
    
    @Builder.Default
    private Integer currentBacklog = 0;
    
    @Builder.Default
    private Integer totalBacklog = 0;
    
    private String uploadBatch;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}