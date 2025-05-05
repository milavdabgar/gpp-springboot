package in.gppalanpur.portal.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gppalanpur.portal.dto.auth.JwtResponse;
import in.gppalanpur.portal.dto.auth.LoginRequest;
import in.gppalanpur.portal.dto.auth.SignupRequest;
import in.gppalanpur.portal.dto.auth.SwitchRoleRequest;
import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.User;
import in.gppalanpur.portal.exception.ResourceNotFoundException;
import in.gppalanpur.portal.exception.UnauthorizedException;
import in.gppalanpur.portal.repository.DepartmentRepository;
import in.gppalanpur.portal.repository.UserRepository;
import in.gppalanpur.portal.security.JwtUtils;
import in.gppalanpur.portal.security.UserDetailsImpl;
import in.gppalanpur.portal.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public JwtResponse signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        
        // Set roles (default to student if not provided)
        if (signupRequest.getRoles() == null || signupRequest.getRoles().isEmpty()) {
            user.setRoles(java.util.Collections.singletonList("student"));
        } else {
            user.setRoles(signupRequest.getRoles());
        }
        
        // Set selected role
        if (signupRequest.getSelectedRole() != null) {
            if (!user.getRoles().contains(signupRequest.getSelectedRole())) {
                throw new IllegalArgumentException("Selected role must be one of the assigned roles");
            }
            user.setSelectedRole(signupRequest.getSelectedRole());
        } else {
            user.setSelectedRole(user.getRoles().get(0));
        }
        
        // Set department if provided
        if (signupRequest.getDepartmentId() != null) {
            Department department = departmentRepository.findById(signupRequest.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            user.setDepartment(department);
        }
        
        // Save user
        userRepository.save(user);
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signupRequest.getEmail(), signupRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken((UserDetailsImpl) authentication.getPrincipal());
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        return JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .name(userDetails.getName())
                .email(userDetails.getUsername())
                .roles(userDetails.getRoles())
                .selectedRole(userDetails.getSelectedRole())
                .build();
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Update selected role if provided
        if (loginRequest.getSelectedRole() != null) {
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            if (!user.getRoles().contains(loginRequest.getSelectedRole())) {
                throw new UnauthorizedException("Invalid role selected");
            }
            
            user.setSelectedRole(loginRequest.getSelectedRole());
            userRepository.save(user);
            userDetails = UserDetailsImpl.build(user);
        }
        
        String jwt = jwtUtils.generateToken(userDetails);
        
        return JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .name(userDetails.getName())
                .email(userDetails.getUsername())
                .roles(userDetails.getRoles())
                .selectedRole(userDetails.getSelectedRole())
                .build();
    }

    @Override
    @Transactional
    public JwtResponse switchRole(String username, SwitchRoleRequest request) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Validate role
        if (!user.getRoles().contains(request.getRole())) {
            throw new UnauthorizedException("Invalid role selected");
        }
        
        // Update selected role
        user.setSelectedRole(request.getRole());
        userRepository.save(user);
        
        // Generate new token
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String jwt = jwtUtils.generateToken(userDetails);
        
        return JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .name(userDetails.getName())
                .email(userDetails.getUsername())
                .roles(userDetails.getRoles())
                .selectedRole(userDetails.getSelectedRole())
                .build();
    }
}