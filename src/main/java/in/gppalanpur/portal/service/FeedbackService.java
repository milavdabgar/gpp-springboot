package in.gppalanpur.portal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.feedback.FeedbackAnalysisResult;
import in.gppalanpur.portal.dto.feedback.FeedbackBatchResponse;
import in.gppalanpur.portal.dto.feedback.FeedbackImportResult;
import in.gppalanpur.portal.dto.feedback.FeedbackResponse;

public interface FeedbackService {
    
    /**
     * Get all feedback with pagination
     * 
     * @param year Academic year (optional)
     * @param term Term (optional)
     * @param branch Branch (optional)
     * @param semester Semester (optional)
     * @param pageable Pagination information
     * @return Page of FeedbackResponse objects
     */
    Page<FeedbackResponse> getAllFeedback(String year, String term, String branch, Integer semester, Pageable pageable);
    
    /**
     * Get a feedback by ID
     * 
     * @param id Feedback ID
     * @return FeedbackResponse object
     */
    FeedbackResponse getFeedback(Long id);
    
    /**
     * Import feedback from a CSV file
     * 
     * @param file CSV file containing feedback data
     * @param userId ID of the user performing the import
     * @return FeedbackImportResult containing import statistics
     */
    FeedbackImportResult importFeedback(MultipartFile file, Long userId);
    
    /**
     * Export feedback to a CSV file
     * 
     * @return Byte array containing CSV data
     */
    byte[] exportFeedback();
    
    /**
     * Get a sample CSV file for feedback import
     * 
     * @return String containing sample CSV data
     */
    String getSampleCsv();
    
    /**
     * Analyze feedback data
     * 
     * @param year Academic year (optional)
     * @param term Term (optional)
     * @param branch Branch (optional)
     * @param semester Semester (optional)
     * @return FeedbackAnalysisResult containing analysis results
     */
    FeedbackAnalysisResult analyzeFeedback(String year, String term, String branch, Integer semester);
    
    /**
     * Generate a PDF report of feedback analysis
     * 
     * @param year Academic year (optional)
     * @param term Term (optional)
     * @param branch Branch (optional)
     * @param semester Semester (optional)
     * @return Byte array containing PDF data
     */
    byte[] generatePdfReport(String year, String term, String branch, Integer semester);
    
    /**
     * Generate an Excel report of feedback analysis
     * 
     * @param year Academic year (optional)
     * @param term Term (optional)
     * @param branch Branch (optional)
     * @param semester Semester (optional)
     * @return Byte array containing Excel data
     */
    byte[] generateExcelReport(String year, String term, String branch, Integer semester);
    
    /**
     * Get all upload batches with pagination
     * 
     * @param pageable Pagination information
     * @return List of FeedbackBatchResponse objects
     */
    List<FeedbackBatchResponse> getUploadBatches(Pageable pageable);
    
    /**
     * Delete all feedback in a batch
     * 
     * @param batchId Batch ID
     * @return Number of feedback entries deleted
     */
    int deleteFeedbackByBatch(String batchId);
    
    /**
     * Delete a feedback by ID
     * 
     * @param id Feedback ID
     */
    void deleteFeedback(Long id);
}
