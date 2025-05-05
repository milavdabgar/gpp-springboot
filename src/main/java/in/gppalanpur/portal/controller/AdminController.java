package in.gppalanpur.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gppalanpur.portal.dto.ApiResponse;
import in.gppalanpur.portal.dto.PaginatedResponse;
import in.gppalanpur.portal.dto.admin.CreateUserRequest;
import in.gppalanpur.portal.dto.admin.UpdateUserRequest;
import in.gppalanpur.portal.dto.admin.UserResponse;
import in.gppalanpur.portal.dto.admin.UserRoleRequest;
import in.gppalanpur.portal.security.UserDetailsImpl;
import in.gppalanpur.portal.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_admin')")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management API")
public class AdminController {

    private final AdminService adminService;
    
    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<UserResponse> usersPage = adminService.getAllUsers(pageable);
        
        PaginatedResponse<UserResponse> paginatedResponse = PaginatedResponse.<UserResponse>builder()
                .page(usersPage.getNumber() + 1)
                .limit(usersPage.getSize())
                .total(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .build();
        
        ApiResponse<List<UserResponse>> response = ApiResponse.<List<UserResponse>>builder()
                .status("success")
                .message("Users retrieved successfully")
                .data(Map.of("users", usersPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        UserResponse user = adminService.getUser(id);
        
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status("success")
                .message("User retrieved successfully")
                .data(Map.of("user", user))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/users")
    @Operation(summary = "Create a new user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        UserResponse user = adminService.createUser(request, userDetails.getId());
        
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status("success")
                .message("User created successfully")
                .data(Map.of("user", user))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/users/{id}")
    @Operation(summary = "Update a user")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        UserResponse user = adminService.updateUser(id, request, userDetails.getId());
        
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status("success")
                .message("User updated successfully")
                .data(Map.of("user", user))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        adminService.deleteUser(id, userDetails.getId());
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .message("User deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/users/{id}/roles")
    @Operation(summary = "Update user roles")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        UserResponse user = adminService.updateUserRoles(id, request, userDetails.getId());
        
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status("success")
                .message("User roles updated successfully")
                .data(Map.of("user", user))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = adminService.getDashboardStats();
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Dashboard statistics retrieved successfully")
                .data(Map.of("stats", stats))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/roles")
    @Operation(summary = "Get all available roles")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllRoles() {
        List<String> roleNames = adminService.getAllRoles();
        
        // Transform the roles into a list of maps with all properties expected by the React frontend
        List<Map<String, Object>> formattedRoles = roleNames.stream()
                .map(role -> {
                    Map<String, Object> roleMap = new HashMap<>();
                    roleMap.put("_id", role); // Use role name as ID
                    roleMap.put("name", role);
                    roleMap.put("description", "Role for " + role + " users");
                    roleMap.put("permissions", new ArrayList<String>()); // Empty permissions array
                    roleMap.put("createdAt", new java.util.Date().toString());
                    roleMap.put("updatedAt", new java.util.Date().toString());
                    return roleMap;
                })
                .collect(Collectors.toList());
        
        ApiResponse<List<Map<String, Object>>> response = ApiResponse.<List<Map<String, Object>>>builder()
                .status("success")
                .message("Roles retrieved successfully")
                .data(Map.of("roles", formattedRoles))
                .build();
        
        return ResponseEntity.ok(response);
    }
}