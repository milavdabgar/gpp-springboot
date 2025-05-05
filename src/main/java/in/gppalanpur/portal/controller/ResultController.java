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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.ApiResponse;
import in.gppalanpur.portal.dto.PaginatedResponse;
import in.gppalanpur.portal.dto.result.ResultAnalysisResponse;
import in.gppalanpur.portal.dto.result.ResultBatchResponse;
import in.gppalanpur.portal.dto.result.ResultImportResult;
import in.gppalanpur.portal.dto.result.ResultResponse;
import in.gppalanpur.portal.security.UserDetailsImpl;
import in.gppalanpur.portal.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
@Tag(name = "Results", description = "Result management API")
public class ResultController {

    private final ResultService resultService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal', 'ROLE_faculty')")
    @Operation(summary = "Get all results")
    public ResponseEntity<ApiResponse<List<ResultResponse>>> getAllResults(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<ResultResponse> resultsPage = resultService.getAllResults(pageable);
        
        PaginatedResponse<ResultResponse> paginatedResponse = PaginatedResponse.<ResultResponse>builder()
                .page(resultsPage.getNumber() + 1)
                .limit(resultsPage.getSize())
                .total(resultsPage.getTotalElements())
                .totalPages(resultsPage.getTotalPages())
                .build();
        
        ApiResponse<List<ResultResponse>> response = ApiResponse.<List<ResultResponse>>builder()
                .status("success")
                .message("Results retrieved successfully")
                .data(Map.of("results", resultsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal', 'ROLE_faculty')")
    @Operation(summary = "Get result by ID")
    public ResponseEntity<ApiResponse<ResultResponse>> getResult(@PathVariable Long id) {
        ResultResponse result = resultService.getResult(id);
        
        ApiResponse<ResultResponse> response = ApiResponse.<ResultResponse>builder()
                .status("success")
                .message("Result retrieved successfully")
                .data(Map.of("result", result))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/student/{enrollmentNo}")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal', 'ROLE_faculty', 'ROLE_student')")
    @Operation(summary = "Get results for a student by enrollment number")
    public ResponseEntity<ApiResponse<List<ResultResponse>>> getStudentResults(
            @PathVariable String enrollmentNo,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<ResultResponse> resultsPage = resultService.getStudentResults(enrollmentNo, pageable);
        
        PaginatedResponse<ResultResponse> paginatedResponse = PaginatedResponse.<ResultResponse>builder()
                .page(resultsPage.getNumber() + 1)
                .limit(resultsPage.getSize())
                .total(resultsPage.getTotalElements())
                .totalPages(resultsPage.getTotalPages())
                .build();
        
        ApiResponse<List<ResultResponse>> response = ApiResponse.<List<ResultResponse>>builder()
                .status("success")
                .message("Student results retrieved successfully")
                .data(Map.of("results", resultsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
    @Operation(summary = "Import results from CSV")
    public ResponseEntity<ApiResponse<ResultImportResult>> importResults(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ResultImportResult importResult = resultService.importResults(file, userDetails.getId());
        
        ApiResponse<ResultImportResult> response = ApiResponse.<ResultImportResult>builder()
                .status("success")
                .message("Results imported successfully")
                .data(Map.of("importResult", importResult))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
    @Operation(summary = "Export results to CSV")
    public ResponseEntity<byte[]> exportResults() {
        byte[] csvContent = resultService.exportResults();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "results.csv");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }
    
    @GetMapping("/analysis")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
    @Operation(summary = "Get branch-wise result analysis")
    public ResponseEntity<ApiResponse<List<ResultAnalysisResponse>>> getBranchAnalysis(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) Integer examId) {
        
        List<ResultAnalysisResponse> analysis = resultService.getBranchAnalysis(academicYear, examId);
        
        ApiResponse<List<ResultAnalysisResponse>> response = ApiResponse.<List<ResultAnalysisResponse>>builder()
                .status("success")
                .message("Branch analysis retrieved successfully")
                .data(Map.of("analysis", analysis))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/batches")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
    @Operation(summary = "Get upload batches")
    public ResponseEntity<ApiResponse<List<ResultBatchResponse>>> getUploadBatches(
            @PageableDefault(size = 10) Pageable pageable) {
        
        List<ResultBatchResponse> batches = resultService.getUploadBatches(pageable);
        
        ApiResponse<List<ResultBatchResponse>> response = ApiResponse.<List<ResultBatchResponse>>builder()
                .status("success")
                .message("Upload batches retrieved successfully")
                .data(Map.of("batches", batches))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/batch/{batchId}")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
    @Operation(summary = "Delete results by batch")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> deleteResultsByBatch(@PathVariable String batchId) {
        int count = resultService.deleteResultsByBatch(batchId);
        
        Map<String, Integer> countMap = Map.of("deletedCount", count);
        ApiResponse<Map<String, Integer>> response = ApiResponse.<Map<String, Integer>>builder()
                .status("success")
                .message("Results deleted successfully")
                .data(Map.of("result", countMap))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
    @Operation(summary = "Delete a result")
    public ResponseEntity<ApiResponse<Void>> deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .message("Result deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
}
