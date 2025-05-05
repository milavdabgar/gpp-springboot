package in.gppalanpur.portal.dto.admin;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleRequest {
    
    @NotEmpty(message = "Roles cannot be empty")
    private List<String> roles;
    
    private String selectedRole;
}
