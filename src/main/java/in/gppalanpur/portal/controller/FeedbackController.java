package in.gppalanpur.portal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import in.gppalanpur.portal.dto.ApiResponse;
import in.gppalanpur.portal.dto.PaginatedResponse;
import in.gppalanpur.portal.dto.feedback.FeedbackAnalysisResult;
import in.gppalanpur.portal.dto.feedback.FeedbackBatchResponse;
import in.gppalanpur.portal.dto.feedback.FeedbackImportResult;
import in.gppalanpur.portal.dto.feedback.FeedbackResponse;
import in.gppalanpur.portal.service.FeedbackService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Feedback Management APIs")
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    @GetMapping
    @Operation(summary = "Get all feedback")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getAllFeedback(
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer semester,
            Pageable pageable) {
        
        Page<FeedbackResponse> page = feedbackService.getAllFeedback(year, term, branch, semester, pageable);
        
        PaginatedResponse<FeedbackResponse> paginatedResponse = PaginatedResponse.<FeedbackResponse>builder()
                .page(page.getNumber() + 1)
                .limit(page.getSize())
                .total(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
        
        ApiResponse<List<FeedbackResponse>> response = ApiResponse.<List<FeedbackResponse>>builder()
                .status("success")
                .message("Feedback fetched successfully")
                .data(Map.of("feedback", page.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get feedback by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> getFeedback(@PathVariable Long id) {
        FeedbackResponse feedback = feedbackService.getFeedback(id);
        
        ApiResponse<FeedbackResponse> response = ApiResponse.<FeedbackResponse>builder()
                .status("success")
                .message("Feedback fetched successfully")
                .data(Map.of("feedback", feedback))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/import")
    @Operation(summary = "Import feedback from CSV")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackImportResult>> importFeedback(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        
        FeedbackImportResult result = feedbackService.importFeedback(file, userId);
        
        ApiResponse<FeedbackImportResult> response = ApiResponse.<FeedbackImportResult>builder()
                .status("success")
                .message("Feedback imported successfully")
                .data(Map.of("result", result))
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/export")
    @Operation(summary = "Export feedback to CSV")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<byte[]> exportFeedback() {
        byte[] csvBytes = feedbackService.exportFeedback();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "feedback_export.csv");
        
        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }
    
    @GetMapping("/sample-csv")
    @Operation(summary = "Get sample CSV for feedback import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getSampleCsv() {
        String sampleCsv = feedbackService.getSampleCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        
        return new ResponseEntity<>(sampleCsv, headers, HttpStatus.OK);
    }
    
    @GetMapping("/analysis")
    @Operation(summary = "Analyze feedback data")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<ApiResponse<FeedbackAnalysisResult>> analyzeFeedback(
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer semester) {
        
        FeedbackAnalysisResult result = feedbackService.analyzeFeedback(year, term, branch, semester);
        
        ApiResponse<FeedbackAnalysisResult> response = ApiResponse.<FeedbackAnalysisResult>builder()
                .status("success")
                .message("Feedback analysis completed successfully")
                .data(Map.of("analysis", result))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/report/pdf")
    @Operation(summary = "Generate PDF report")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<byte[]> generatePdfReport(
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer semester) {
        
        byte[] pdfBytes = feedbackService.generatePdfReport(year, term, branch, semester);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "feedback_report.pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
    
    @GetMapping("/report/excel")
    @Operation(summary = "Generate Excel report")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<byte[]> generateExcelReport(
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer semester) {
        
        byte[] excelBytes = feedbackService.generateExcelReport(year, term, branch, semester);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "feedback_report.xlsx");
        
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
    
    @GetMapping("/batches")
    @Operation(summary = "Get upload batches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FeedbackBatchResponse>>> getUploadBatches(Pageable pageable) {
        List<FeedbackBatchResponse> batches = feedbackService.getUploadBatches(pageable);
        
        ApiResponse<List<FeedbackBatchResponse>> response = ApiResponse.<List<FeedbackBatchResponse>>builder()
                .status("success")
                .message("Upload batches fetched successfully")
                .data(Map.of("batches", batches))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/batches/{batchId}")
    @Operation(summary = "Delete feedback by batch ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> deleteFeedbackByBatch(@PathVariable String batchId) {
        int count = feedbackService.deleteFeedbackByBatch(batchId);
        
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .status("success")
                .message("Feedback batch deleted successfully")
                .data(Map.of("count", count))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete feedback by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .message("Feedback deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
}
