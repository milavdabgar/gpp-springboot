package in.gppalanpur.portal.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gppalanpur.portal.dto.user.UserDetailsResponse;
import in.gppalanpur.portal.dto.user.UserProfileUpdateRequest;
import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.User;
import in.gppalanpur.portal.exception.ResourceNotFoundException;
import in.gppalanpur.portal.repository.DepartmentRepository;
import in.gppalanpur.portal.repository.UserRepository;
import in.gppalanpur.portal.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public UserDetailsResponse getCurrentUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return mapToUserDetailsResponse(user);
    }

    @Override
    @Transactional
    public UserDetailsResponse updateUserProfile(String username, UserProfileUpdateRequest request) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Update user details
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        
        if (request.getEmail() != null && !user.getEmail().equals(request.getEmail())) {
            // Check if email is already in use
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email is already in use");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            user.setDepartment(department);
        }
        
        userRepository.save(user);
        
        return mapToUserDetailsResponse(user);
    }
    
    private UserDetailsResponse mapToUserDetailsResponse(User user) {
        return UserDetailsResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .selectedRole(user.getSelectedRole())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .build();
    }
}