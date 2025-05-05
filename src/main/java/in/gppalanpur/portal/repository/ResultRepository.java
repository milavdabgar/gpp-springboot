package in.gppalanpur.portal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    
    List<Result> findByEnrollmentNo(String enrollmentNo);
    
    Optional<Result> findByEnrollmentNoAndExamId(String enrollmentNo, Integer examId);
    
    List<Result> findByBranchName(String branchName);
    
    List<Result> findBySemester(Integer semester);
    
    List<Result> findByAcademicYear(String academicYear);
    
    List<Result> findByUploadBatch(String uploadBatch);
    
    Page<Result> findByBranchNameAndSemester(String branchName, Integer semester, Pageable pageable);
    
    @Query("SELECT DISTINCT r.uploadBatch, COUNT(r) as count, MAX(r.createdAt) as latestUpload FROM Result r GROUP BY r.uploadBatch ORDER BY latestUpload DESC")
    List<Object[]> findUploadBatches(Pageable pageable);
    
    @Query("SELECT r.branchName, r.semester, AVG(r.spi) as avgSpi, " +
           "SUM(CASE WHEN r.result = 'PASS' THEN 1 ELSE 0 END) as passCount, " +
           "COUNT(r) as totalCount " +
           "FROM Result r " +
           "WHERE (:academicYear IS NULL OR r.academicYear = :academicYear) " +
           "AND (:examId IS NULL OR r.examId = :examId) " +
           "GROUP BY r.branchName, r.semester")
    List<Object[]> getBranchAnalysis(@Param("academicYear") String academicYear, @Param("examId") Integer examId);
}