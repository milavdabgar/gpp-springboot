package in.gppalanpur.portal.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Project {
    
    public enum Status {
        DRAFT, PENDING, SUBMITTED, APPROVED, REJECTED, COMPLETED
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String category;
    
    @NotBlank
    @Column(columnDefinition = "text")
    private String abstract_;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.DRAFT;
    
    @Embedded
    @Builder.Default
    private ProjectRequirements requirements = new ProjectRequirements();
    
    // Guide information
    @Column(name = "guide_name")
    private String guideName;
    
    @Column(name = "guide_email")
    private String guideEmail;
    
    @Column(name = "guide_phone")
    private String guidePhone;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "completed", column = @Column(name = "dept_evaluation_completed")),
        @AttributeOverride(name = "score", column = @Column(name = "dept_evaluation_score")),
        @AttributeOverride(name = "feedback", column = @Column(name = "dept_evaluation_feedback")),
        @AttributeOverride(name = "evaluatedAt", column = @Column(name = "dept_evaluation_evaluated_at")),
        @AttributeOverride(name = "juryId", column = @Column(name = "dept_jury_id"))
    })
    private ProjectEvaluation deptEvaluation;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "completed", column = @Column(name = "central_evaluation_completed")),
        @AttributeOverride(name = "score", column = @Column(name = "central_evaluation_score")),
        @AttributeOverride(name = "feedback", column = @Column(name = "central_evaluation_feedback")),
        @AttributeOverride(name = "evaluatedAt", column = @Column(name = "central_evaluation_evaluated_at")),
        @AttributeOverride(name = "juryId", column = @Column(name = "central_jury_id"))
    })
    private ProjectEvaluation centralEvaluation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}