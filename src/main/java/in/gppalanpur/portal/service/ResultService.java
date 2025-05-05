package in.gppalanpur.portal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.result.ResultAnalysisResponse;
import in.gppalanpur.portal.dto.result.ResultBatchResponse;
import in.gppalanpur.portal.dto.result.ResultImportResult;
import in.gppalanpur.portal.dto.result.ResultResponse;

public interface ResultService {
    
    /**
     * Get all results with pagination
     * 
     * @param pageable Pagination information
     * @return Page of ResultResponse objects
     */
    Page<ResultResponse> getAllResults(Pageable pageable);
    
    /**
     * Get a result by ID
     * 
     * @param id Result ID
     * @return ResultResponse object
     */
    ResultResponse getResult(Long id);
    
    /**
     * Get results for a student by enrollment number
     * 
     * @param enrollmentNo Student enrollment number
     * @param pageable Pagination information
     * @return Page of ResultResponse objects
     */
    Page<ResultResponse> getStudentResults(String enrollmentNo, Pageable pageable);
    
    /**
     * Import results from a CSV file
     * 
     * @param file CSV file containing result data
     * @param userId ID of the user performing the import
     * @return ResultImportResult containing import statistics
     */
    ResultImportResult importResults(MultipartFile file, Long userId);
    
    /**
     * Export results to a CSV file
     * 
     * @return Byte array containing CSV data
     */
    byte[] exportResults();
    
    /**
     * Get branch-wise result analysis
     * 
     * @param academicYear Academic year for filtering (optional)
     * @param examId Exam ID for filtering (optional)
     * @return List of ResultAnalysisResponse objects
     */
    List<ResultAnalysisResponse> getBranchAnalysis(String academicYear, Integer examId);
    
    /**
     * Get all upload batches with pagination
     * 
     * @param pageable Pagination information
     * @return List of ResultBatchResponse objects
     */
    List<ResultBatchResponse> getUploadBatches(Pageable pageable);
    
    /**
     * Delete all results in a batch
     * 
     * @param batchId Batch ID
     * @return Number of results deleted
     */
    int deleteResultsByBatch(String batchId);
    
    /**
     * Delete a result by ID
     * 
     * @param id Result ID
     */
    void deleteResult(Long id);
}
