package in.gppalanpur.portal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    
    @NotBlank
    @Column(unique = true)
    private String enrollmentNo;
    
    @Email
    private String personalEmail;
    
    @NotBlank
    @Email
    @Column(unique = true)
    private String institutionalEmail;
    
    private String batch;
    
    @NotNull
    @Builder.Default
    private Integer semester = 1;
    
    @Embedded
    @Builder.Default
    private SemesterStatus semesterStatus = new SemesterStatus();
    
    @Builder.Default
    private String status = "active";
    
    @Embedded
    @Builder.Default
    private Guardian guardian = new Guardian();
    
    @Embedded
    @Builder.Default
    private Contact contact = new Contact();
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudentEducation> educationBackground = new ArrayList<>();
    
    private String gender;
    private String category;
    private String aadharNo;
    
    @NotNull
    private Integer admissionYear;
    
    private Integer convoYear;
    
    @Builder.Default
    private Boolean isComplete = false;
    
    @Builder.Default
    private Boolean termClose = false;
    
    @Builder.Default
    private Boolean isCancel = false;
    
    @Builder.Default
    private Boolean isPassAll = false;
    
    @Builder.Default
    private Integer shift = 1;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}