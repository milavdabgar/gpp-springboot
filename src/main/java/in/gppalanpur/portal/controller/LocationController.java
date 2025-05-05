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
import in.gppalanpur.portal.dto.location.CreateLocationBatchRequest;
import in.gppalanpur.portal.dto.location.CreateLocationRequest;
import in.gppalanpur.portal.dto.location.LocationImportResult;
import in.gppalanpur.portal.dto.location.LocationResponse;
import in.gppalanpur.portal.dto.location.UpdateLocationRequest;
import in.gppalanpur.portal.security.UserDetailsImpl;
import in.gppalanpur.portal.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "Location management API")
public class LocationController {

    private final LocationService locationService;
    
    @GetMapping
    @Operation(summary = "Get all locations")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getAllLocations(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<LocationResponse> locationsPage = locationService.getAllLocations(pageable);
        
        PaginatedResponse<LocationResponse> paginatedResponse = PaginatedResponse.<LocationResponse>builder()
                .page(locationsPage.getNumber() + 1)
                .limit(locationsPage.getSize())
                .total(locationsPage.getTotalElements())
                .totalPages(locationsPage.getTotalPages())
                .build();
        
        ApiResponse<List<LocationResponse>> response = ApiResponse.<List<LocationResponse>>builder()
                .status("success")
                .message("Locations retrieved successfully")
                .data(Map.of("locations", locationsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocation(@PathVariable Long id) {
        LocationResponse location = locationService.getLocation(id);
        
        ApiResponse<LocationResponse> response = ApiResponse.<LocationResponse>builder()
                .status("success")
                .message("Location retrieved successfully")
                .data(Map.of("location", location))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Create a new location")
    public ResponseEntity<ApiResponse<LocationResponse>> createLocation(
            @Valid @RequestBody CreateLocationRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        LocationResponse location = locationService.createLocation(request, userDetails.getId());
        
        ApiResponse<LocationResponse> response = ApiResponse.<LocationResponse>builder()
                .status("success")
                .message("Location created successfully")
                .data(Map.of("location", location))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Update a location")
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLocationRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        LocationResponse location = locationService.updateLocation(id, request, userDetails.getId());
        
        ApiResponse<LocationResponse> response = ApiResponse.<LocationResponse>builder()
                .status("success")
                .message("Location updated successfully")
                .data(Map.of("location", location))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Delete a location")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .message("Location deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active locations")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getActiveLocations(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<LocationResponse> locationsPage = locationService.getActiveLocations(pageable);
        
        PaginatedResponse<LocationResponse> paginatedResponse = PaginatedResponse.<LocationResponse>builder()
                .page(locationsPage.getNumber() + 1)
                .limit(locationsPage.getSize())
                .total(locationsPage.getTotalElements())
                .totalPages(locationsPage.getTotalPages())
                .build();
        
        ApiResponse<List<LocationResponse>> response = ApiResponse.<List<LocationResponse>>builder()
                .status("success")
                .message("Active locations retrieved successfully")
                .data(Map.of("locations", locationsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get locations by department")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getLocationsByDepartment(
            @PathVariable Long departmentId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<LocationResponse> locationsPage = locationService.getLocationsByDepartment(departmentId, pageable);
        
        PaginatedResponse<LocationResponse> paginatedResponse = PaginatedResponse.<LocationResponse>builder()
                .page(locationsPage.getNumber() + 1)
                .limit(locationsPage.getSize())
                .total(locationsPage.getTotalElements())
                .totalPages(locationsPage.getTotalPages())
                .build();
        
        ApiResponse<List<LocationResponse>> response = ApiResponse.<List<LocationResponse>>builder()
                .status("success")
                .message("Department locations retrieved successfully")
                .data(Map.of("locations", locationsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/section/{section}")
    @Operation(summary = "Get locations by section")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getLocationsBySection(
            @PathVariable String section,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<LocationResponse> locationsPage = locationService.getLocationsBySection(section, pageable);
        
        PaginatedResponse<LocationResponse> paginatedResponse = PaginatedResponse.<LocationResponse>builder()
                .page(locationsPage.getNumber() + 1)
                .limit(locationsPage.getSize())
                .total(locationsPage.getTotalElements())
                .totalPages(locationsPage.getTotalPages())
                .build();
        
        ApiResponse<List<LocationResponse>> response = ApiResponse.<List<LocationResponse>>builder()
                .status("success")
                .message("Section locations retrieved successfully")
                .data(Map.of("locations", locationsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get locations by event")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getLocationsByEvent(
            @PathVariable Long eventId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<LocationResponse> locationsPage = locationService.getLocationsByEvent(eventId, pageable);
        
        PaginatedResponse<LocationResponse> paginatedResponse = PaginatedResponse.<LocationResponse>builder()
                .page(locationsPage.getNumber() + 1)
                .limit(locationsPage.getSize())
                .total(locationsPage.getTotalElements())
                .totalPages(locationsPage.getTotalPages())
                .build();
        
        ApiResponse<List<LocationResponse>> response = ApiResponse.<List<LocationResponse>>builder()
                .status("success")
                .message("Event locations retrieved successfully")
                .data(Map.of("locations", locationsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/assign/{projectId}")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Assign a project to a location")
    public ResponseEntity<ApiResponse<LocationResponse>> assignProjectToLocation(
            @PathVariable Long id,
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        LocationResponse location = locationService.assignProjectToLocation(id, projectId, userDetails.getId());
        
        ApiResponse<LocationResponse> response = ApiResponse.<LocationResponse>builder()
                .status("success")
                .message("Project assigned to location successfully")
                .data(Map.of("location", location))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/unassign")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Unassign a project from a location")
    public ResponseEntity<ApiResponse<LocationResponse>> unassignProjectFromLocation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        LocationResponse location = locationService.unassignProjectFromLocation(id, userDetails.getId());
        
        ApiResponse<LocationResponse> response = ApiResponse.<LocationResponse>builder()
                .status("success")
                .message("Project unassigned from location successfully")
                .data(Map.of("location", location))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
    @Operation(summary = "Get location statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLocationStatistics() {
        Map<String, Object> statistics = locationService.getLocationStatistics();
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Location statistics retrieved successfully")
                .data(Map.of("statistics", statistics))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/import")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Import locations from CSV")
    public ResponseEntity<ApiResponse<LocationImportResult>> importLocations(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        LocationImportResult result = locationService.importLocationsFromCsv(file, userDetails.getId());
        
        ApiResponse<LocationImportResult> response = ApiResponse.<LocationImportResult>builder()
                .status("success")
                .message("Locations imported successfully")
                .data(Map.of("importResult", result))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/export")
    @Operation(summary = "Export locations to CSV")
    public ResponseEntity<byte[]> exportLocations() {
        byte[] csvContent = locationService.exportLocationsToCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "locations.csv");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }
    
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Create multiple locations in batch")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> createLocationBatch(
            @Valid @RequestBody CreateLocationBatchRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<LocationResponse> locations = locationService.createLocationBatch(request, userDetails.getId());
        
        ApiResponse<List<LocationResponse>> response = ApiResponse.<List<LocationResponse>>builder()
                .status("success")
                .message("Locations created successfully")
                .data(Map.of("locations", locations))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
