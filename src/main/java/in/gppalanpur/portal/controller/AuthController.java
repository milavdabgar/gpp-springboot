package in.gppalanpur.portal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gppalanpur.portal.dto.auth.JwtResponse;
import in.gppalanpur.portal.dto.auth.LoginRequest;
import in.gppalanpur.portal.dto.auth.SignupRequest;
import in.gppalanpur.portal.dto.auth.SwitchRoleRequest;
import in.gppalanpur.portal.security.UserDetailsImpl;
import in.gppalanpur.portal.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<JwtResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        JwtResponse response = authService.signup(signupRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate a user and get a JWT token")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/switch-role")
    @Operation(summary = "Switch the user's active role")
    public ResponseEntity<JwtResponse> switchRole(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody SwitchRoleRequest request) {
        JwtResponse response = authService.switchRole(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }
}