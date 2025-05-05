package in.gppalanpur.portal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.admin.CreateUserRequest;
import in.gppalanpur.portal.dto.admin.RoleAssignmentRequest;
import in.gppalanpur.portal.dto.admin.UpdateUserRequest;
import in.gppalanpur.portal.dto.admin.UserCsvImportResult;
import in.gppalanpur.portal.dto.admin.UserResponse;
import in.gppalanpur.portal.dto.admin.UserSearchCriteria;

public interface AdminService {

    // User Management
    UserResponse createUser(CreateUserRequest request);
    
    Page<UserResponse> getAllUsers(UserSearchCriteria criteria, Pageable pageable);
    
    UserResponse getUserById(Long id);
    
    UserResponse updateUser(Long id, UpdateUserRequest request);
    
    void deleteUser(Long id);
    
    UserResponse assignRoles(Long userId, RoleAssignmentRequest request);
    
    // CSV Import/Export
    UserCsvImportResult importUsers(MultipartFile file);
    
    byte[] exportUsers();
    
    // Role Management
    List<String> getAllRoles();
}