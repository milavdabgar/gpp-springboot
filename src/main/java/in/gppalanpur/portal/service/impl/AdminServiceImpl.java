package in.gppalanpur.portal.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.admin.CreateUserRequest;

import in.gppalanpur.portal.dto.admin.UpdateUserRequest;
import in.gppalanpur.portal.dto.admin.UserCsvImportResult;
import in.gppalanpur.portal.dto.admin.UserResponse;
import in.gppalanpur.portal.dto.admin.UserRoleRequest;
import in.gppalanpur.portal.dto.admin.UserSearchCriteria;
import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.User;
import in.gppalanpur.portal.exception.BadRequestException;
import in.gppalanpur.portal.exception.ResourceNotFoundException;
import in.gppalanpur.portal.repository.DepartmentRepository;
import in.gppalanpur.portal.repository.UserRepository;
import in.gppalanpur.portal.service.AdminService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Using a CopyOnWriteArrayList for thread safety
    private static final List<String> AVAILABLE_ROLES = new java.util.concurrent.CopyOnWriteArrayList<>(
            Arrays.asList("student", "faculty", "hod", "principal", "admin", "jury"));
    
    private static final String[] CSV_HEADERS = {
            "Name", "Email", "Department", "Roles", "Selected Role"
    };

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request, Long creatorId) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }
        
        // Validate department if provided
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }
        
        // Validate roles
        List<String> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = List.of("student"); // Default role
        } else {
            validateRoles(roles);
        }
        
        // Validate selected role
        String selectedRole = request.getSelectedRole();
        if (selectedRole == null || selectedRole.isEmpty()) {
            selectedRole = roles.get(0); // Default to first role
        } else if (!roles.contains(selectedRole)) {
            throw new BadRequestException("Selected role must be one of the assigned roles");
        }
        
        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .selectedRole(selectedRole)
                .department(department)
                .build();
        
        User savedUser = userRepository.save(user);
        
        return mapToUserResponse(savedUser);
    }

    @Override
    public Page<UserResponse> getAllUsers(UserSearchCriteria criteria, Pageable pageable) {
        // Create specification for filtering
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Search by name or email
            if (criteria.getSearch() != null && !criteria.getSearch().isEmpty()) {
                String search = "%" + criteria.getSearch().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), search),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), search)
                ));
            }
            
            // Filter by role
            if (criteria.getRole() != null && !criteria.getRole().isEmpty() && !criteria.getRole().equals("all")) {
                predicates.add(criteriaBuilder.isMember(criteria.getRole(), root.get("roles")));
            }
            
            // Filter by department
            if (criteria.getDepartmentId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("department").get("id"), criteria.getDepartmentId()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // Apply sorting
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "name";
        String sortOrder = criteria.getSortOrder() != null ? criteria.getSortOrder() : "asc";
        Direction direction = sortOrder.equalsIgnoreCase("desc") ? Direction.DESC : Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        
        // Execute query
        // Create a new PageRequest with the sort parameter
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<User> users = userRepository.findAll(spec, pageRequest);
        
        // Map to response
        return users.map(this::mapToUserResponse);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToUserResponse);
    }

    @Override
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, Long updaterId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Update name if provided
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        
        // Update email if provided
        if (request.getEmail() != null && !request.getEmail().isEmpty() 
                && !request.getEmail().equals(user.getEmail())) {
            
            // Check email uniqueness
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email is already in use");
            }
            
            user.setEmail(request.getEmail());
        }
        
        // Update department if provided
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            user.setDepartment(department);
        }
        
        User updatedUser = userRepository.save(user);
        
        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Long deleterId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserRoles(Long userId, UserRoleRequest request, Long updaterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Log who is updating the roles
        log.info("User roles being updated by user ID: {}", updaterId);
        
        List<String> roles = request.getRoles();
        
        // Validate roles
        validateRoles(roles);
        
        // Update roles
        user.setRoles(roles);
        
        // Update selected role if needed
        if (!roles.contains(user.getSelectedRole())) {
            user.setSelectedRole(roles.get(0));
        }
        
        User updatedUser = userRepository.save(user);
        
        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserCsvImportResult importUsers(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Please upload a CSV file");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".csv")) {
            throw new BadRequestException("Please upload a valid CSV file");
        }
        
        List<UserResponse> importedUsers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {
            
            for (CSVRecord record : csvParser) {
                try {
                    // Extract fields from CSV
                    String name = record.get("Name");
                    String email = record.get("Email");
                    String departmentName = record.get("Department");
                    String rolesStr = record.get("Roles");
                    String selectedRole = record.get("Selected Role");
                    
                    // Check if user already exists
                    if (userRepository.existsByEmail(email)) {
                        errors.add("Skipping user " + name + ": Email '" + email + "' already exists");
                        continue;
                    }
                    
                    // Find department by name
                    Department department = null;
                    if (departmentName != null && !departmentName.isEmpty()) {
                        department = departmentRepository.findByName(departmentName)
                                .orElse(null);
                        
                        if (department == null) {
                            errors.add("Skipping user " + name + ": Department '" + departmentName + "' not found");
                            continue;
                        }
                    }
                    
                    // Parse roles
                    List<String> roles = new ArrayList<>();
                    if (rolesStr != null && !rolesStr.isEmpty()) {
                        roles = Arrays.stream(rolesStr.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                    }
                    
                    // Default to 'faculty' if no roles provided
                    if (roles.isEmpty()) {
                        roles.add("faculty");
                    }
                    
                    // Validate roles
                    try {
                        validateRoles(roles);
                    } catch (BadRequestException e) {
                        errors.add("Skipping user " + name + ": " + e.getMessage());
                        continue;
                    }
                    
                    // Validate selected role
                    if (selectedRole == null || selectedRole.isEmpty()) {
                        selectedRole = roles.get(0);
                    } else if (!roles.contains(selectedRole)) {
                        errors.add("Skipping user " + name + ": Selected role '" + selectedRole 
                                + "' is not one of the assigned roles: " + String.join(", ", roles));
                        continue;
                    }
                    
                    // Create user
                    User user = User.builder()
                            .name(name)
                            .email(email)
                            .password(passwordEncoder.encode("User@123")) // Default password
                            .roles(roles)
                            .selectedRole(selectedRole)
                            .department(department)
                            .build();
                    
                    User savedUser = userRepository.save(user);
                    importedUsers.add(mapToUserResponse(savedUser));
                    
                } catch (Exception e) {
                    errors.add("Error processing row: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            throw new BadRequestException("Failed to parse CSV file: " + e.getMessage());
        }
        
        // Build summary
        String summary = String.format("Successfully imported %d users. %d errors encountered.", 
                importedUsers.size(), errors.size());
        
        return UserCsvImportResult.builder()
                .successful(importedUsers.size())
                .failed(errors.size())
                .summary(summary)
                .users(importedUsers)
                .errors(errors)
                .build();
    }

    @Override
    public byte[] exportUsers() {
        List<User> users = userRepository.findAll();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(CSV_HEADERS))) {
            
            for (User user : users) {
                Department department = user.getDepartment();
                
                csvPrinter.printRecord(
                        user.getName(),
                        user.getEmail(),
                        department != null ? department.getName() : "",
                        String.join(", ", user.getRoles()),
                        user.getSelectedRole()
                );
            }
            
            csvPrinter.flush();
            return out.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to export users: " + e.getMessage());
        }
    }

    @Override
    public List<String> getAllRoles() {
        return new ArrayList<>(AVAILABLE_ROLES);
    }
    
    @Override
    public Map<String, Object> getRole(String roleId) {
        // Validate role exists
        if (!AVAILABLE_ROLES.contains(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }
        
        // Create role object with appropriate permissions based on role type
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("_id", roleId);
        roleMap.put("name", roleId);
        roleMap.put("description", "Role for " + roleId + " users");
        
        // Add CRUD permissions based on role type
        List<String> permissions = new ArrayList<>();
        
        // According to the Express model, permissions are: create, read, update, delete
        if ("admin".equals(roleId)) {
            // Admin has all permissions
            permissions.add("create");
            permissions.add("read");
            permissions.add("update");
            permissions.add("delete");
        } else if ("faculty".equals(roleId) || "hod".equals(roleId) || "principal".equals(roleId)) {
            // Faculty, HOD, and Principal have create, read, update permissions
            permissions.add("create");
            permissions.add("read");
            permissions.add("update");
        } else if ("jury".equals(roleId)) {
            // Jury has read and update permissions
            permissions.add("read");
            permissions.add("update");
        } else if ("student".equals(roleId)) {
            // Students have read permission only
            permissions.add("read");
        }
        roleMap.put("permissions", permissions);
        
        // Format dates in ISO format
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(new java.util.Date());
        roleMap.put("createdAt", formattedDate);
        roleMap.put("updatedAt", formattedDate);
        
        return roleMap;
    }
    
    @Override
    public Map<String, Object> createRole(in.gppalanpur.portal.dto.admin.RoleUpdateRequest request) {
        // Validate role name
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Role name is required");
        }
        
        // Validate permissions
        List<String> validPermissions = Arrays.asList("create", "read", "update", "delete");
        if (request.getPermissions() != null) {
            for (String permission : request.getPermissions()) {
                if (!validPermissions.contains(permission)) {
                    throw new BadRequestException("Invalid permission: " + permission);
                }
            }
        }
        
        String roleId = request.getName().toLowerCase();
        
        // Check if the role already exists
        boolean roleExists = AVAILABLE_ROLES.contains(roleId);
        
        // If the role doesn't exist, add it to AVAILABLE_ROLES
        if (!roleExists) {
            AVAILABLE_ROLES.add(roleId);
            log.info("Added new role: {}", roleId);
        }
        
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("_id", roleId);
        roleMap.put("name", request.getName());
        roleMap.put("description", request.getDescription() != null ? request.getDescription() : "Role for " + roleId + " users");
        roleMap.put("permissions", request.getPermissions() != null ? request.getPermissions() : new ArrayList<>());
        
        // Format dates in ISO format
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(new java.util.Date());
        roleMap.put("createdAt", formattedDate);
        roleMap.put("updatedAt", formattedDate);
        
        return roleMap;
    }
    
    @Override
    public Map<String, Object> updateRole(String roleId, in.gppalanpur.portal.dto.admin.RoleUpdateRequest request) {
        // Validate role exists
        if (!AVAILABLE_ROLES.contains(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }
        
        // Validate permissions
        List<String> validPermissions = Arrays.asList("create", "read", "update", "delete");
        if (request.getPermissions() != null) {
            for (String permission : request.getPermissions()) {
                if (!validPermissions.contains(permission)) {
                    throw new BadRequestException("Invalid permission: " + permission);
                }
            }
        }
        
        // Handle role name changes
        String newRoleId = request.getName() != null ? request.getName().toLowerCase() : roleId;
        
        // If the role name is changing, update AVAILABLE_ROLES
        if (!roleId.equals(newRoleId)) {
            // Remove the old role ID
            AVAILABLE_ROLES.remove(roleId);
            
            // Add the new role ID if it doesn't already exist
            if (!AVAILABLE_ROLES.contains(newRoleId)) {
                AVAILABLE_ROLES.add(newRoleId);
            }
            
            log.info("Updated role from {} to {}", roleId, newRoleId);
        }
        
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("_id", newRoleId);
        roleMap.put("name", request.getName() != null ? request.getName() : roleId);
        roleMap.put("description", request.getDescription() != null ? request.getDescription() : "Role for " + newRoleId + " users");
        roleMap.put("permissions", request.getPermissions() != null ? request.getPermissions() : new ArrayList<>());
        
        // Format dates in ISO format
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(new java.util.Date());
        roleMap.put("createdAt", formattedDate);
        roleMap.put("updatedAt", formattedDate);
        
        return roleMap;
    }
    
    @Override
    public Map<String, Object> deleteRole(String roleId) {
        // Validate role exists
        if (!AVAILABLE_ROLES.contains(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }
        
        // Check if it's a default role that shouldn't be deleted
        List<String> defaultRoles = Arrays.asList("student", "faculty", "hod", "principal", "admin");
        if (defaultRoles.contains(roleId)) {
            throw new BadRequestException("Cannot delete default role: " + roleId);
        }
        
        // Remove the role from AVAILABLE_ROLES
        boolean removed = AVAILABLE_ROLES.remove(roleId);
        
        if (removed) {
            log.info("Deleted role: {}", roleId);
        } else {
            log.warn("Failed to delete role: {}", roleId);
            throw new BadRequestException("Failed to delete role: " + roleId);
        }
        
        // Return a success response
        Map<String, Object> response = new HashMap<>();
        response.put("_id", roleId);
        response.put("deleted", true);
        
        return response;
    }
    
    @Override
    public byte[] exportRoles() {
        List<String> roleNames = getAllRoles();
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("name", "description", "permissions", "createdAt", "updatedAt")
                     .build())) {
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String formattedDate = sdf.format(new java.util.Date());
            
            for (String roleName : roleNames) {
                List<String> permissions = new ArrayList<>();
                
                // Add CRUD permissions based on role type
                if ("admin".equals(roleName)) {
                    permissions.add("create,read,update,delete");
                } else if ("faculty".equals(roleName) || "hod".equals(roleName) || "principal".equals(roleName)) {
                    permissions.add("create,read,update");
                } else if ("jury".equals(roleName)) {
                    permissions.add("read,update");
                } else if ("student".equals(roleName)) {
                    permissions.add("read");
                }
                
                printer.printRecord(
                        roleName,
                        "Role for " + roleName + " users",
                        String.join(",", permissions),
                        formattedDate,
                        formattedDate
                );
            }
            
            printer.flush();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error exporting roles to CSV", e);
            throw new RuntimeException("Error exporting roles to CSV", e);
        }
    }
    
    @Override
    public Map<String, Object> importRoles(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("CSV file is empty");
        }
        
        // Get existing roles to preserve them
        List<String> existingRoleNames = getAllRoles();
        
        // Create a list to store all roles (existing + imported)
        List<Map<String, Object>> allRoles = new ArrayList<>();
        
        // First, add all existing roles to the result list
        for (String roleName : existingRoleNames) {
            Map<String, Object> roleMap = getRole(roleName); // Reuse our existing getRole method
            allRoles.add(roleMap);
        }
        
        // Now process the imported roles
        List<Map<String, Object>> importedRoles = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalCount = 0;
        int successCount = 0;
        int updatedCount = 0;
        int newCount = 0;
        
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build())) {
            
            for (CSVRecord record : csvParser) {
                totalCount++;
                
                try {
                    String name = record.get("name");
                    String description = record.get("description");
                    String permissionsStr = record.get("permissions");
                    
                    // Validate name
                    if (name == null || name.trim().isEmpty()) {
                        errors.add("Row " + totalCount + ": Name is required");
                        continue;
                    }
                    
                    // Validate permissions
                    List<String> permissions = new ArrayList<>();
                    if (permissionsStr != null && !permissionsStr.trim().isEmpty()) {
                        String[] permArray = permissionsStr.split(",");
                        for (String perm : permArray) {
                            String trimmedPerm = perm.trim();
                            if (!Arrays.asList("create", "read", "update", "delete").contains(trimmedPerm)) {
                                errors.add("Row " + totalCount + ": Invalid permission: " + trimmedPerm);
                                continue;
                            }
                            permissions.add(trimmedPerm);
                        }
                    }
                    
                    // Check if this is an update to an existing role or a new role
                    boolean isUpdate = false;
                    String roleLowerCase = name.toLowerCase();
                    
                    // Look for the role in our existing roles
                    for (int i = 0; i < allRoles.size(); i++) {
                        Map<String, Object> existingRole = allRoles.get(i);
                        if (existingRole.get("_id").equals(roleLowerCase)) {
                            // Update the existing role
                            existingRole.put("name", name);
                            existingRole.put("description", description != null ? description : "Role for " + name + " users");
                            existingRole.put("permissions", permissions);
                            
                            // Format dates in ISO format
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                            String formattedDate = sdf.format(new java.util.Date());
                            existingRole.put("updatedAt", formattedDate);
                            
                            isUpdate = true;
                            updatedCount++;
                            break;
                        }
                    }
                    
                    // If not an update, create a new role
                    if (!isUpdate) {
                        Map<String, Object> roleMap = new HashMap<>();
                        roleMap.put("_id", roleLowerCase);
                        roleMap.put("name", name);
                        roleMap.put("description", description != null ? description : "Role for " + name + " users");
                        roleMap.put("permissions", permissions);
                        
                        // Format dates in ISO format
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                        String formattedDate = sdf.format(new java.util.Date());
                        roleMap.put("createdAt", formattedDate);
                        roleMap.put("updatedAt", formattedDate);
                        
                        allRoles.add(roleMap);
                        newCount++;
                    }
                    
                    // Add to imported roles list for the response
                    Map<String, Object> importedRoleMap = new HashMap<>();
                    importedRoleMap.put("_id", roleLowerCase);
                    importedRoleMap.put("name", name);
                    importedRoleMap.put("description", description != null ? description : "Role for " + name + " users");
                    importedRoleMap.put("permissions", permissions);
                    importedRoles.add(importedRoleMap);
                    
                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + totalCount + ": " + e.getMessage());
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", totalCount);
            result.put("successCount", successCount);
            result.put("updatedCount", updatedCount);
            result.put("newCount", newCount);
            result.put("errorCount", errors.size());
            result.put("errors", errors);
            result.put("roles", importedRoles);
            result.put("allRoles", allRoles);
            
            return result;
        } catch (IOException e) {
            log.error("Error importing roles from CSV", e);
            throw new RuntimeException("Error importing roles from CSV", e);
        }
    }
    
    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Count users by role
        Map<String, Long> usersByRole = new HashMap<>();
        for (String role : AVAILABLE_ROLES) {
            // Use a custom query or count manually since countByRolesContaining might not be available
            long count = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .count();
            usersByRole.put(role, count);
        }
        stats.put("usersByRole", usersByRole);
        
        // Total users
        stats.put("totalUsers", userRepository.count());
        
        // Users by department
        List<Department> departments = departmentRepository.findAll();
        Map<String, Long> usersByDepartment = new HashMap<>();
        for (Department dept : departments) {
            // Use a custom query or count manually since countByDepartment might not be available
            long count = userRepository.findAll().stream()
                .filter(user -> user.getDepartment() != null && user.getDepartment().getId().equals(dept.getId()))
                .count();
            usersByDepartment.put(dept.getName(), count);
        }
        stats.put("usersByDepartment", usersByDepartment);
        
        return stats;
    }
    
    private void validateRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new BadRequestException("At least one role must be assigned");
        }
        
        for (String role : roles) {
            if (!AVAILABLE_ROLES.contains(role)) {
                throw new BadRequestException("Invalid role: " + role + ". Available roles: " 
                        + String.join(", ", AVAILABLE_ROLES));
            }
        }
    }
    
    private UserResponse mapToUserResponse(User user) {
        Department department = user.getDepartment();
        
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .selectedRole(user.getSelectedRole())
                .departmentId(department != null ? department.getId() : null)
                .departmentName(department != null ? department.getName() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}