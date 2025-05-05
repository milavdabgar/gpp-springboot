package in.gppalanpur.portal.entity;

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
public class ProjectRequirements {
    
    @Builder.Default
    private Boolean power = false;
    
    @Builder.Default
    private Boolean internet = false;
    
    @Builder.Default
    private Boolean specialSpace = false;
    
    @Column(columnDefinition = "text")
    @Builder.Default
    private String otherRequirements = "";
}