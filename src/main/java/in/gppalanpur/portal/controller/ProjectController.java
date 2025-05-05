package in.gppalanpur.portal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.ApiResponse;
import in.gppalanpur.portal.dto.PaginatedResponse;
import in.gppalanpur.portal.dto.project.CreateProjectRequest;
import in.gppalanpur.portal.dto.project.EvaluateProjectRequest;
import in.gppalanpur.portal.dto.project.ProjectDetailsResponse;
import in.gppalanpur.portal.dto.project.ProjectImportResult;
import in.gppalanpur.portal.dto.project.ProjectResponse;
import in.gppalanpur.portal.dto.project.UpdateProjectRequest;
import in.gppalanpur.portal.security.UserDetailsImpl;
import in.gppalanpur.portal.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management API")
public class ProjectController {

    private final ProjectService projectService;
    
    @GetMapping
    @Operation(summary = "Get all projects")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProjectResponse> projectsPage = projectService.getAllProjects(pageable);
        
        PaginatedResponse<ProjectResponse> paginatedResponse = PaginatedResponse.<ProjectResponse>builder()
                .page(projectsPage.getNumber() + 1)
                .limit(projectsPage.getSize())
                .total(projectsPage.getTotalElements())
                .totalPages(projectsPage.getTotalPages())
                .build();
        
        ApiResponse<List<ProjectResponse>> response = ApiResponse.<List<ProjectResponse>>builder()
                .status("success")
                .message("Projects retrieved successfully")
                .data(Map.of("projects", projectsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(@PathVariable Long id) {
        ProjectResponse project = projectService.getProject(id);
        
        ApiResponse<ProjectResponse> response = ApiResponse.<ProjectResponse>builder()
                .status("success")
                .message("Project retrieved successfully")
                .data(Map.of("project", project))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/details")
    @Operation(summary = "Get project details by ID")
    public ResponseEntity<ApiResponse<ProjectDetailsResponse>> getProjectDetails(@PathVariable Long id) {
        ProjectDetailsResponse projectDetails = projectService.getProjectDetails(id);
        
        ApiResponse<ProjectDetailsResponse> response = ApiResponse.<ProjectDetailsResponse>builder()
                .status("success")
                .message("Project details retrieved successfully")
                .data(Map.of("project", projectDetails))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ProjectResponse project = projectService.createProject(request, userDetails.getId());
        
        ApiResponse<ProjectResponse> response = ApiResponse.<ProjectResponse>builder()
                .status("success")
                .message("Project created successfully")
                .data(Map.of("project", project))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a project")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ProjectResponse project = projectService.updateProject(id, request, userDetails.getId());
        
        ApiResponse<ProjectResponse> response = ApiResponse.<ProjectResponse>builder()
                .status("success")
                .message("Project updated successfully")
                .data(Map.of("project", project))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Delete a project")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .message("Project deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-projects")
    @Operation(summary = "Get projects created by the current user")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getMyProjects(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<ProjectResponse> projectsPage = projectService.getProjectsByCreator(userDetails.getId(), pageable);
        
        PaginatedResponse<ProjectResponse> paginatedResponse = PaginatedResponse.<ProjectResponse>builder()
                .page(projectsPage.getNumber() + 1)
                .limit(projectsPage.getSize())
                .total(projectsPage.getTotalElements())
                .totalPages(projectsPage.getTotalPages())
                .build();
        
        ApiResponse<List<ProjectResponse>> response = ApiResponse.<List<ProjectResponse>>builder()
                .status("success")
                .message("My projects retrieved successfully")
                .data(Map.of("projects", projectsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get projects by department")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByDepartment(
            @PathVariable Long departmentId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<ProjectResponse> projectsPage = projectService.getProjectsByDepartment(departmentId, pageable);
        
        PaginatedResponse<ProjectResponse> paginatedResponse = PaginatedResponse.<ProjectResponse>builder()
                .page(projectsPage.getNumber() + 1)
                .limit(projectsPage.getSize())
                .total(projectsPage.getTotalElements())
                .totalPages(projectsPage.getTotalPages())
                .build();
        
        ApiResponse<List<ProjectResponse>> response = ApiResponse.<List<ProjectResponse>>builder()
                .status("success")
                .message("Department projects retrieved successfully")
                .data(Map.of("projects", projectsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get projects by event")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByEvent(
            @PathVariable Long eventId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<ProjectResponse> projectsPage = projectService.getProjectsByEvent(eventId, pageable);
        
        PaginatedResponse<ProjectResponse> paginatedResponse = PaginatedResponse.<ProjectResponse>builder()
                .page(projectsPage.getNumber() + 1)
                .limit(projectsPage.getSize())
                .total(projectsPage.getTotalElements())
                .totalPages(projectsPage.getTotalPages())
                .build();
        
        ApiResponse<List<ProjectResponse>> response = ApiResponse.<List<ProjectResponse>>builder()
                .status("success")
                .message("Event projects retrieved successfully")
                .data(Map.of("projects", projectsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get projects by team")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByTeam(
            @PathVariable Long teamId) {
        
        List<ProjectResponse> projects = projectService.getProjectsByTeam(teamId);
        
        ApiResponse<List<ProjectResponse>> response = ApiResponse.<List<ProjectResponse>>builder()
                .status("success")
                .message("Team projects retrieved successfully")
                .data(Map.of("projects", projects))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get project statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProjectStatistics() {
        Map<String, Object> statistics = projectService.getProjectStatistics();
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Project statistics retrieved successfully")
                .data(Map.of("statistics", statistics))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Get project counts by category")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getProjectCountsByCategory(
            @RequestParam(required = false) Long eventId) {
        
        Map<String, Long> categoryCounts = projectService.getProjectCountsByCategory(eventId);
        
        ApiResponse<Map<String, Long>> response = ApiResponse.<Map<String, Long>>builder()
                .status("success")
                .message("Project category counts retrieved successfully")
                .data(Map.of("categories", categoryCounts))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/import")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Import projects from CSV file")
    public ResponseEntity<ApiResponse<ProjectImportResult>> importProjects(
            @RequestBody MultipartFile file) {
        
        ProjectImportResult result = projectService.importProjects(file);
        
        ApiResponse<ProjectImportResult> response = ApiResponse.<ProjectImportResult>builder()
                .status("success")
                .message("Projects imported successfully")
                .data(Map.of("importResult", result))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/export")
    @Operation(summary = "Export projects to CSV")
    public ResponseEntity<byte[]> exportProjects() {
        byte[] csvFile = projectService.exportProjects();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "projects.csv");
        
        return new ResponseEntity<>(csvFile, headers, HttpStatus.OK);
    }
    
    @PostMapping("/{id}/department-evaluation")
    @PreAuthorize("hasRole('ROLE_jury')")
    @Operation(summary = "Evaluate project by department jury")
    public ResponseEntity<ApiResponse<ProjectResponse>> evaluateProjectByDepartment(
            @PathVariable Long id,
            @Valid @RequestBody EvaluateProjectRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ProjectResponse project = projectService.evaluateProjectByDepartment(id, request, userDetails.getId());
        
        ApiResponse<ProjectResponse> response = ApiResponse.<ProjectResponse>builder()
                .status("success")
                .message("Project evaluated by department successfully")
                .data(Map.of("project", project))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/central-evaluation")
    @PreAuthorize("hasRole('ROLE_jury')")
    @Operation(summary = "Evaluate project by central jury")
    public ResponseEntity<ApiResponse<ProjectResponse>> evaluateProjectByCentral(
            @PathVariable Long id,
            @Valid @RequestBody EvaluateProjectRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ProjectResponse project = projectService.evaluateProjectByCentral(id, request, userDetails.getId());
        
        ApiResponse<ProjectResponse> response = ApiResponse.<ProjectResponse>builder()
                .status("success")
                .message("Project evaluated by central jury successfully")
                .data(Map.of("project", project))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/jury-assignments")
    @PreAuthorize("hasRole('ROLE_jury')")
    @Operation(summary = "Get projects assigned to the current jury")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsForJury(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<ProjectResponse> projects = projectService.getProjectsForJury(userDetails.getId());
        
        ApiResponse<List<ProjectResponse>> response = ApiResponse.<List<ProjectResponse>>builder()
                .status("success")
                .message("Jury assignments retrieved successfully")
                .data(Map.of("projects", projects))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/event/{eventId}/winners")
    @Operation(summary = "Get event winners")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getEventWinners(
            @PathVariable Long eventId) {
        
        List<ProjectResponse> winners = projectService.getEventWinners(eventId);
        
        ApiResponse<List<ProjectResponse>> response = ApiResponse.<List<ProjectResponse>>builder()
                .status("success")
                .message("Event winners retrieved successfully")
                .data(Map.of("winners", winners))
                .build();
        
        return ResponseEntity.ok(response);
    }
}
