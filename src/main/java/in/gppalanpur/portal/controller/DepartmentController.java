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

@RestController
@RequestMapping("/departments")
@PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Department management API")
public class DepartmentController {

    private final DepartmentService departmentService;
    
    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable Long id) {
        DepartmentResponse department = departmentService.getDepartment(id);
        return ResponseEntity.ok(department);
    }
    
    @PostMapping
    @Operation(summary = "Create a new department")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentResponse department = departmentService.createDepartment(request);
        return new ResponseEntity<>(department, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateDepartmentRequest request) {
        
        DepartmentResponse department = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(department);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get department statistics")
    public ResponseEntity<Map<String, Object>> getDepartmentStats() {
        Map<String, Object> stats = departmentService.getDepartmentStats();
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/import")
    @Operation(summary = "Import departments from CSV file")
    public ResponseEntity<DepartmentImportResult> importDepartments(@RequestBody MultipartFile file) {
        DepartmentImportResult result = departmentService.importDepartments(file);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/export")
    @Operation(summary = "Export departments to CSV")
    public ResponseEntity<byte[]> exportDepartments() {
        byte[] csvFile = departmentService.exportDepartments();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "departments.csv");
        
        return new ResponseEntity<>(csvFile, headers, HttpStatus.OK);
    }
}