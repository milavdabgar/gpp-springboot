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
    private Boolean needsElectricity = false;
    
    @Builder.Default
    private Boolean needsWater = false;
    
    @Builder.Default
    private Boolean needsGas = false;
    
    @Builder.Default
    private Boolean needsInternet = false;
    
    @Builder.Default
    private Boolean needsDisplay = false;
    
    @Builder.Default
    private Boolean needsExtraSpace = false;
    
    @Column(columnDefinition = "text")
    @Builder.Default
    private String otherRequirements = "";
}