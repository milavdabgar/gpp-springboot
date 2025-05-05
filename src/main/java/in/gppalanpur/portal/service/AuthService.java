package in.gppalanpur.portal.service;

import in.gppalanpur.portal.dto.auth.JwtResponse;
import in.gppalanpur.portal.dto.auth.LoginRequest;
import in.gppalanpur.portal.dto.auth.SignupRequest;
import in.gppalanpur.portal.dto.auth.SwitchRoleRequest;

public interface AuthService {

    JwtResponse signup(SignupRequest signupRequest);
    
    JwtResponse login(LoginRequest loginRequest);
    
    JwtResponse switchRole(String username, SwitchRoleRequest request);
}