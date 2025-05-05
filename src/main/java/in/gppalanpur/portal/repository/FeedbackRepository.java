package in.gppalanpur.portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    List<Feedback> findByBatchId(String batchId);
    
    List<Feedback> findByYearAndTerm(String year, String term);
    
    List<Feedback> findByBranch(String branch);
    
    List<Feedback> findBySemester(Integer semester);
    
    List<Feedback> findBySubjectCode(String subjectCode);
    
    List<Feedback> findByFacultyName(String facultyName);
    
    Page<Feedback> findByYearAndTermAndBranch(String year, String term, String branch, Pageable pageable);
    
    @Query("SELECT DISTINCT f.batchId, COUNT(f) as count, MAX(f.createdAt) as latestUpload FROM Feedback f GROUP BY f.batchId ORDER BY latestUpload DESC")
    List<Object[]> findUploadBatches(Pageable pageable);
    
    @Query("SELECT f.subjectCode, f.subjectName, AVG(CAST(f.ratings['Q1'] AS double)) as q1, " +
           "AVG(CAST(f.ratings['Q2'] AS double)) as q2, " +
           "AVG(CAST(f.ratings['Q3'] AS double)) as q3, " +
           "AVG(CAST(f.ratings['Q4'] AS double)) as q4, " +
           "AVG(CAST(f.ratings['Q5'] AS double)) as q5, " +
           "AVG(CAST(f.ratings['Q6'] AS double)) as q6, " +
           "AVG(CAST(f.ratings['Q7'] AS double)) as q7, " +
           "AVG(CAST(f.ratings['Q8'] AS double)) as q8, " +
           "AVG(CAST(f.ratings['Q9'] AS double)) as q9, " +
           "AVG(CAST(f.ratings['Q10'] AS double)) as q10, " +
           "AVG(CAST(f.ratings['Q11'] AS double)) as q11, " +
           "AVG(CAST(f.ratings['Q12'] AS double)) as q12, " +
           "COUNT(f) as count " +
           "FROM Feedback f " +
           "WHERE (:year IS NULL OR f.year = :year) " +
           "AND (:term IS NULL OR f.term = :term) " +
           "AND (:branch IS NULL OR f.branch = :branch) " +
           "AND (:semester IS NULL OR f.semester = :semester) " +
           "GROUP BY f.subjectCode, f.subjectName")
    List<Object[]> getSubjectAnalysis(@Param("year") String year, @Param("term") String term, 
                                      @Param("branch") String branch, @Param("semester") Integer semester);
    
    @Query("SELECT f.facultyName, AVG(CAST(f.ratings['Q1'] AS double)) as q1, " +
           "AVG(CAST(f.ratings['Q2'] AS double)) as q2, " +
           "AVG(CAST(f.ratings['Q3'] AS double)) as q3, " +
           "AVG(CAST(f.ratings['Q4'] AS double)) as q4, " +
           "AVG(CAST(f.ratings['Q5'] AS double)) as q5, " +
           "AVG(CAST(f.ratings['Q6'] AS double)) as q6, " +
           "AVG(CAST(f.ratings['Q7'] AS double)) as q7, " +
           "AVG(CAST(f.ratings['Q8'] AS double)) as q8, " +
           "AVG(CAST(f.ratings['Q9'] AS double)) as q9, " +
           "AVG(CAST(f.ratings['Q10'] AS double)) as q10, " +
           "AVG(CAST(f.ratings['Q11'] AS double)) as q11, " +
           "AVG(CAST(f.ratings['Q12'] AS double)) as q12, " +
           "COUNT(f) as count " +
           "FROM Feedback f " +
           "WHERE (:year IS NULL OR f.year = :year) " +
           "AND (:term IS NULL OR f.term = :term) " +
           "AND (:branch IS NULL OR f.branch = :branch) " +
           "AND (:semester IS NULL OR f.semester = :semester) " +
           "GROUP BY f.facultyName")
    List<Object[]> getFacultyAnalysis(@Param("year") String year, @Param("term") String term, 
                                     @Param("branch") String branch, @Param("semester") Integer semester);
    
    @Query("SELECT f.semester, AVG(CAST(f.ratings['Q1'] AS double)) as q1, " +
           "AVG(CAST(f.ratings['Q2'] AS double)) as q2, " +
           "AVG(CAST(f.ratings['Q3'] AS double)) as q3, " +
           "AVG(CAST(f.ratings['Q4'] AS double)) as q4, " +
           "AVG(CAST(f.ratings['Q5'] AS double)) as q5, " +
           "AVG(CAST(f.ratings['Q6'] AS double)) as q6, " +
           "AVG(CAST(f.ratings['Q7'] AS double)) as q7, " +
           "AVG(CAST(f.ratings['Q8'] AS double)) as q8, " +
           "AVG(CAST(f.ratings['Q9'] AS double)) as q9, " +
           "AVG(CAST(f.ratings['Q10'] AS double)) as q10, " +
           "AVG(CAST(f.ratings['Q11'] AS double)) as q11, " +
           "AVG(CAST(f.ratings['Q12'] AS double)) as q12, " +
           "COUNT(f) as count " +
           "FROM Feedback f " +
           "WHERE (:year IS NULL OR f.year = :year) " +
           "AND (:term IS NULL OR f.term = :term) " +
           "AND (:branch IS NULL OR f.branch = :branch) " +
           "GROUP BY f.semester")
    List<Object[]> getSemesterAnalysis(@Param("year") String year, @Param("term") String term, 
                                      @Param("branch") String branch);
    
    @Query("SELECT f.branch, AVG(CAST(f.ratings['Q1'] AS double)) as q1, " +
           "AVG(CAST(f.ratings['Q2'] AS double)) as q2, " +
           "AVG(CAST(f.ratings['Q3'] AS double)) as q3, " +
           "AVG(CAST(f.ratings['Q4'] AS double)) as q4, " +
           "AVG(CAST(f.ratings['Q5'] AS double)) as q5, " +
           "AVG(CAST(f.ratings['Q6'] AS double)) as q6, " +
           "AVG(CAST(f.ratings['Q7'] AS double)) as q7, " +
           "AVG(CAST(f.ratings['Q8'] AS double)) as q8, " +
           "AVG(CAST(f.ratings['Q9'] AS double)) as q9, " +
           "AVG(CAST(f.ratings['Q10'] AS double)) as q10, " +
           "AVG(CAST(f.ratings['Q11'] AS double)) as q11, " +
           "AVG(CAST(f.ratings['Q12'] AS double)) as q12, " +
           "COUNT(f) as count " +
           "FROM Feedback f " +
           "WHERE (:year IS NULL OR f.year = :year) " +
           "AND (:term IS NULL OR f.term = :term) " +
           "GROUP BY f.branch")
    List<Object[]> getBranchAnalysis(@Param("year") String year, @Param("term") String term);
    
    @Query("SELECT CONCAT(f.year, '-', f.term) as yearTerm, AVG(CAST(f.ratings['Q1'] AS double)) as q1, " +
           "AVG(CAST(f.ratings['Q2'] AS double)) as q2, " +
           "AVG(CAST(f.ratings['Q3'] AS double)) as q3, " +
           "AVG(CAST(f.ratings['Q4'] AS double)) as q4, " +
           "AVG(CAST(f.ratings['Q5'] AS double)) as q5, " +
           "AVG(CAST(f.ratings['Q6'] AS double)) as q6, " +
           "AVG(CAST(f.ratings['Q7'] AS double)) as q7, " +
           "AVG(CAST(f.ratings['Q8'] AS double)) as q8, " +
           "AVG(CAST(f.ratings['Q9'] AS double)) as q9, " +
           "AVG(CAST(f.ratings['Q10'] AS double)) as q10, " +
           "AVG(CAST(f.ratings['Q11'] AS double)) as q11, " +
           "AVG(CAST(f.ratings['Q12'] AS double)) as q12, " +
           "COUNT(f) as count " +
           "FROM Feedback f " +
           "GROUP BY CONCAT(f.year, '-', f.term)")
    List<Object[]> getTermYearAnalysis();
}
