package in.gppalanpur.portal.dto.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String status;
    private String message;
    private Map<String, Object> data;
    private String token;
    
    /**
     * Factory method to create a JwtResponse with the expected format for the React frontend
     */
    public static JwtResponse createResponse(String token, UserDto user) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        
        return JwtResponse.builder()
                .status("success")
                .message("Authentication successful")
                .data(data)
                .token(token)
                .build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private Long id;
        private String name;
        private String email;
        private List<String> roles;
        private String selectedRole;
        private Long departmentId;
    }
}