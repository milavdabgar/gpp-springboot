package in.gppalanpur.portal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.department.CreateDepartmentRequest;
import in.gppalanpur.portal.dto.department.DepartmentImportResult;
import in.gppalanpur.portal.dto.department.DepartmentResponse;
import in.gppalanpur.portal.dto.department.UpdateDepartmentRequest;
import in.gppalanpur.portal.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/departments")
@PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Department management API")
public class DepartmentController {

    private final DepartmentService departmentService;
    
    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<in.gppalanpur.portal.dto.ApiResponse<List<DepartmentResponse>>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        
        in.gppalanpur.portal.dto.ApiResponse<List<DepartmentResponse>> response = in.gppalanpur.portal.dto.ApiResponse.<List<DepartmentResponse>>builder()
                .status("success")
                .message("Departments retrieved successfully")
                .data(Map.of("departments", departments))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<in.gppalanpur.portal.dto.ApiResponse<DepartmentResponse>> getDepartment(@PathVariable Long id) {
        DepartmentResponse department = departmentService.getDepartment(id);
        
        in.gppalanpur.portal.dto.ApiResponse<DepartmentResponse> response = in.gppalanpur.portal.dto.ApiResponse.<DepartmentResponse>builder()
                .status("success")
                .message("Department retrieved successfully")
                .data(Map.of("department", department))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(summary = "Create a new department")
    public ResponseEntity<in.gppalanpur.portal.dto.ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentResponse department = departmentService.createDepartment(request);
        
        in.gppalanpur.portal.dto.ApiResponse<DepartmentResponse> response = in.gppalanpur.portal.dto.ApiResponse.<DepartmentResponse>builder()
                .status("success")
                .message("Department created successfully")
                .data(Map.of("department", department))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    public ResponseEntity<in.gppalanpur.portal.dto.ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateDepartmentRequest request) {
        
        DepartmentResponse department = departmentService.updateDepartment(id, request);
        
        in.gppalanpur.portal.dto.ApiResponse<DepartmentResponse> response = in.gppalanpur.portal.dto.ApiResponse.<DepartmentResponse>builder()
                .status("success")
                .message("Department updated successfully")
                .data(Map.of("department", department))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department")
    public ResponseEntity<in.gppalanpur.portal.dto.ApiResponse<Void>> deleteDepartment(@PathVariable(required = false) String id) {
        try {
            if (id == null || id.equals("undefined") || id.equals("null")) {
                log.error("Attempt to delete department with invalid ID: {}", id);
                return ResponseEntity.ok(
                    in.gppalanpur.portal.dto.ApiResponse.<Void>builder()
                        .status("error")
                        .message("Invalid department ID")
                        .build()
                );
            }
            
            log.info("Attempting to delete department with ID: {}", id);
            Long departmentId = Long.parseLong(id);
            departmentService.deleteDepartment(departmentId);
            log.info("Successfully deleted department with ID: {}", id);
            
            in.gppalanpur.portal.dto.ApiResponse<Void> response = in.gppalanpur.portal.dto.ApiResponse.<Void>builder()
                    .status("success")
                    .message("Department deleted successfully")
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            log.error("Invalid department ID format: {}", id, e);
            return ResponseEntity.ok(
                in.gppalanpur.portal.dto.ApiResponse.<Void>builder()
                    .status("error")
                    .message("Invalid department ID format: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            log.error("Error deleting department with ID: {}", id, e);
            return ResponseEntity.ok(
                in.gppalanpur.portal.dto.ApiResponse.<Void>builder()
                    .status("error")
                    .message("Failed to delete department: " + e.getMessage())
                    .build()
            );
        }
    }
    
    // Test endpoint removed
    
    @GetMapping("/stats")
    @Operation(summary = "Get department statistics")
    public ResponseEntity<in.gppalanpur.portal.dto.ApiResponse<Map<String, Object>>> getDepartmentStats() {
        Map<String, Object> stats = departmentService.getDepartmentStats();
        
        in.gppalanpur.portal.dto.ApiResponse<Map<String, Object>> response = in.gppalanpur.portal.dto.ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Department statistics retrieved successfully")
                .data(Map.of("stats", stats))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/import")
    @Operation(summary = "Import departments from CSV file")
    public ResponseEntity<in.gppalanpur.portal.dto.ApiResponse<DepartmentImportResult>> importDepartments(@RequestBody MultipartFile file) {
        DepartmentImportResult result = departmentService.importDepartments(file);
        
        in.gppalanpur.portal.dto.ApiResponse<DepartmentImportResult> response = in.gppalanpur.portal.dto.ApiResponse.<DepartmentImportResult>builder()
                .status("success")
                .message("Departments imported successfully")
                .data(Map.of("result", result))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/export")
    @Operation(summary = "Export departments to CSV file")
    public ResponseEntity<byte[]> exportDepartments() {
        byte[] csvBytes = departmentService.exportDepartments();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "departments.csv");
        
        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }
}