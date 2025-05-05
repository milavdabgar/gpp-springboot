package in.gppalanpur.portal.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.admin.CreateUserRequest;
import in.gppalanpur.portal.dto.admin.RoleAssignmentRequest;
import in.gppalanpur.portal.dto.admin.UpdateUserRequest;
import in.gppalanpur.portal.dto.admin.UserCsvImportResult;
import in.gppalanpur.portal.dto.admin.UserResponse;
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
    
    private static final List<String> AVAILABLE_ROLES = Arrays.asList(
            "student", "faculty", "hod", "principal", "admin", "jury");
    
    private static final String[] CSV_HEADERS = {
            "Name", "Email", "Department", "Roles", "Selected Role"
    };

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
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
        Page<User> users = userRepository.findAll(spec, pageable.withSort(sort));
        
        // Map to response
        return users.map(this::mapToUserResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
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
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserResponse assignRoles(Long userId, RoleAssignmentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
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
        
        if (!file.getOriginalFilename().endsWith(".csv")) {
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
        return AVAILABLE_ROLES;
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