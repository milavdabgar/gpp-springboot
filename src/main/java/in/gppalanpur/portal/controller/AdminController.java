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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

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
    
    @GetMapping("/roles/export")
    @Operation(summary = "Export roles as CSV")
    public ResponseEntity<byte[]> exportRoles() {
        byte[] csvData = adminService.exportRoles();
        
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=roles.csv")
                .body(csvData);
    }
    
    @PostMapping("/roles/import")
    @Operation(summary = "Import roles from CSV")
    public ResponseEntity<ApiResponse<Map<String, Object>>> importRoles(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = adminService.importRoles(file);
        
        // Extract the allRoles from the result and use it as the roles in the response
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> allRoles = (List<Map<String, Object>>) result.get("allRoles");
        
        // Create a new result map without the allRoles field to avoid duplication
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", result.get("totalCount"));
        stats.put("successCount", result.get("successCount"));
        stats.put("updatedCount", result.get("updatedCount"));
        stats.put("newCount", result.get("newCount"));
        stats.put("errorCount", result.get("errorCount"));
        stats.put("errors", result.get("errors"));
        
        // Create the response with roles as the main data and stats as metadata
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("roles", allRoles); // This is what the React frontend expects
        responseData.put("stats", stats);    // Additional metadata
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Roles imported successfully")
                .data(Map.of("data", responseData))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/roles/{roleId}")
    @Operation(summary = "Get a single role by ID")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRole(@PathVariable String roleId) {
        Map<String, Object> role = adminService.getRole(roleId);
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Role retrieved successfully")
                .data(Map.of("role", role))
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
                    
                    // Add CRUD permissions based on role type
                    List<String> permissions = new ArrayList<>();
                    
                    // According to the Express model, permissions are: create, read, update, delete
                    if ("admin".equals(role)) {
                        // Admin has all permissions
                        permissions.add("create");
                        permissions.add("read");
                        permissions.add("update");
                        permissions.add("delete");
                    } else if ("faculty".equals(role) || "hod".equals(role) || "principal".equals(role)) {
                        // Faculty, HOD, and Principal have create, read, update permissions
                        permissions.add("create");
                        permissions.add("read");
                        permissions.add("update");
                    } else if ("jury".equals(role)) {
                        // Jury has read and update permissions
                        permissions.add("read");
                        permissions.add("update");
                    } else if ("student".equals(role)) {
                        // Students have read permission only
                        permissions.add("read");
                    }
                    roleMap.put("permissions", permissions);
                    
                    // Format dates in ISO format for better display
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    String formattedDate = sdf.format(new java.util.Date());
                    roleMap.put("createdAt", formattedDate);
                    roleMap.put("updatedAt", formattedDate);
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
    
    @PostMapping("/roles")
    @Operation(summary = "Create a new role")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createRole(
            @Valid @RequestBody in.gppalanpur.portal.dto.admin.RoleUpdateRequest request) {
        
        Map<String, Object> newRole = adminService.createRole(request);
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Role created successfully")
                .data(Map.of("role", newRole))
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PatchMapping("/roles/{roleId}")
    @Operation(summary = "Update a role")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateRole(
            @PathVariable String roleId,
            @Valid @RequestBody in.gppalanpur.portal.dto.admin.RoleUpdateRequest request) {
        
        Map<String, Object> updatedRole = adminService.updateRole(roleId, request);
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Role updated successfully")
                .data(Map.of("role", updatedRole))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/roles/{roleId}")
    @Operation(summary = "Delete a role")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteRole(@PathVariable String roleId) {
        Map<String, Object> result = adminService.deleteRole(roleId);
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Role deleted successfully")
                .data(Map.of("role", result))
                .build();
        
        return ResponseEntity.ok(response);
    }
}
