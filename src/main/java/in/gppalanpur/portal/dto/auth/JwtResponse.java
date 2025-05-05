package in.gppalanpur.portal.dto.auth;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Long id;
    private String name;
    private String email;
    private List<String> roles;
    private String selectedRole;
    private Long departmentId;
}