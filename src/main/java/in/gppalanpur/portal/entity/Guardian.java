package in.gppalanpur.portal.entity;

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
public class Guardian {
    
    @Builder.Default
    private String name = "";
    
    @Builder.Default
    private String relation = "";
    
    @Builder.Default
    private String contact = "";
    
    @Builder.Default
    private String occupation = "";
}