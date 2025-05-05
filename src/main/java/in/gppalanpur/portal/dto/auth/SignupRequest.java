package in.gppalanpur.portal.dto.auth;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
    
    @NotBlank
    @Size(max = 100)
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
    
    private List<String> roles;
    
    private String selectedRole;
    
    private Long departmentId;
}