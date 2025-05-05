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
public class Contact {
    
    @Builder.Default
    private String mobile = "";
    
    @Builder.Default
    private String email = "";
    
    @Builder.Default
    private String address = "";
    
    @Builder.Default
    private String city = "";
    
    @Builder.Default
    private String state = "";
    
    @Builder.Default
    private String pincode = "";
}