package in.gppalanpur.portal.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @Size(min = 3, max = 100)
    private String name;
    
    @Size(max = 100)
    @Email
    private String email;
    
    private Long departmentId;
}