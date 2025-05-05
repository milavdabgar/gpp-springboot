package in.gppalanpur.portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyExperience {
    
    @PositiveOrZero
    @Builder.Default
    private Integer years = 0;
    
    @Column(columnDefinition = "text")
    @Builder.Default
    private String details = "";
}