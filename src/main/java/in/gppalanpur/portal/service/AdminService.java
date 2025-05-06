package in.gppalanpur.portal.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.admin.CreateUserRequest;

import in.gppalanpur.portal.dto.admin.UpdateUserRequest;
import in.gppalanpur.portal.dto.admin.UserCsvImportResult;
import in.gppalanpur.portal.dto.admin.UserResponse;
import in.gppalanpur.portal.dto.admin.UserRoleRequest;
import in.gppalanpur.portal.dto.admin.UserSearchCriteria;

public interface AdminService {

    // User Management
    UserResponse createUser(CreateUserRequest request, Long creatorId);
    
    Page<UserResponse> getAllUsers(Pageable pageable);
    
    Page<UserResponse> getAllUsers(UserSearchCriteria criteria, Pageable pageable);
    
    UserResponse getUser(Long id);
    
    UserResponse updateUser(Long id, UpdateUserRequest request, Long updaterId);
    
    void deleteUser(Long id, Long deleterId);
    
    UserResponse updateUserRoles(Long userId, UserRoleRequest request, Long updaterId);
    
    // CSV Import/Export
    UserCsvImportResult importUsers(MultipartFile file);
    
    byte[] exportUsers();
    
    // Role Management
    List<String> getAllRoles();
    
    Map<String, Object> getRole(String roleId);
    
    Map<String, Object> createRole(in.gppalanpur.portal.dto.admin.RoleUpdateRequest request);
    
    Map<String, Object> updateRole(String roleId, in.gppalanpur.portal.dto.admin.RoleUpdateRequest request);
    
    Map<String, Object> deleteRole(String roleId);
    
    byte[] exportRoles();
    
    Map<String, Object> importRoles(MultipartFile file);
    
    // Dashboard Statistics
    Map<String, Object> getDashboardStats();
}