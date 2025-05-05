package in.gppalanpur.portal.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectGuide {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_user_id")
    private User user;
    
    @NotBlank
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_department_id")
    private Department department;
    
    @NotBlank
    private String contactNumber;
}