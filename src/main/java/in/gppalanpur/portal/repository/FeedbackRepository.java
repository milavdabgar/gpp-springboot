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
    
    @Query(value = "SELECT DISTINCT f.batch_id, COUNT(f) as count, MAX(f.created_at) as latest_upload FROM feedback f GROUP BY f.batch_id ORDER BY latest_upload DESC", nativeQuery = true)
    List<Object[]> findUploadBatches(Pageable pageable);
    
    @Query(value = "SELECT f.subject_code, f.subject_name, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q1') AS double precision)) as q1, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q2') AS double precision)) as q2, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q3') AS double precision)) as q3, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q4') AS double precision)) as q4, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q5') AS double precision)) as q5, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q6') AS double precision)) as q6, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q7') AS double precision)) as q7, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q8') AS double precision)) as q8, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q9') AS double precision)) as q9, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q10') AS double precision)) as q10, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q11') AS double precision)) as q11, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q12') AS double precision)) as q12, " +
           "COUNT(f) as count " +
           "FROM feedback f " +
           "WHERE (:year IS NULL OR f.year = :year) " +
           "AND (:term IS NULL OR f.term = :term) " +
           "AND (:branch IS NULL OR f.branch = :branch) " +
           "AND (:semester IS NULL OR f.semester = :semester) " +
           "GROUP BY f.subject_code, f.subject_name", nativeQuery = true)
    List<Object[]> getSubjectAnalysis(@Param("year") String year, @Param("term") String term, 
                                      @Param("branch") String branch, @Param("semester") Integer semester);
    
    @Query(value = "SELECT f.faculty_name, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q1') AS double precision)) as q1, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q2') AS double precision)) as q2, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q3') AS double precision)) as q3, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q4') AS double precision)) as q4, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q5') AS double precision)) as q5, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q6') AS double precision)) as q6, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q7') AS double precision)) as q7, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q8') AS double precision)) as q8, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q9') AS double precision)) as q9, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q10') AS double precision)) as q10, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q11') AS double precision)) as q11, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q12') AS double precision)) as q12, " +
           "COUNT(f) as count " +
           "FROM feedback f " +
           "WHERE (:year IS NULL OR f.year = :year) " +
           "AND (:term IS NULL OR f.term = :term) " +
           "AND (:branch IS NULL OR f.branch = :branch) " +
           "AND (:semester IS NULL OR f.semester = :semester) " +
           "GROUP BY f.faculty_name", nativeQuery = true)
    List<Object[]> getFacultyAnalysis(@Param("year") String year, @Param("term") String term, 
                                     @Param("branch") String branch, @Param("semester") Integer semester);
    
    @Query(value = "SELECT f.semester, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q1') AS double precision)) as q1, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q2') AS double precision)) as q2, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q3') AS double precision)) as q3, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q4') AS double precision)) as q4, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q5') AS double precision)) as q5, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q6') AS double precision)) as q6, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q7') AS double precision)) as q7, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q8') AS double precision)) as q8, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q9') AS double precision)) as q9, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q10') AS double precision)) as q10, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q11') AS double precision)) as q11, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q12') AS double precision)) as q12, " +
           "COUNT(f) as count " +
           "FROM feedback f " +
           "WHERE (:year IS NULL OR f.year = :year) " +
           "AND (:term IS NULL OR f.term = :term) " +
           "AND (:branch IS NULL OR f.branch = :branch) " +
           "GROUP BY f.semester", nativeQuery = true)
    List<Object[]> getSemesterAnalysis(@Param("year") String year, @Param("term") String term, 
                                      @Param("branch") String branch);
    
    @Query(value = "SELECT f.branch, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q1') AS double precision)) as q1, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q2') AS double precision)) as q2, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q3') AS double precision)) as q3, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q4') AS double precision)) as q4, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q5') AS double precision)) as q5, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q6') AS double precision)) as q6, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q7') AS double precision)) as q7, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q8') AS double precision)) as q8, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q9') AS double precision)) as q9, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q10') AS double precision)) as q10, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q11') AS double precision)) as q11, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q12') AS double precision)) as q12, " +
           "COUNT(f) as count " +
           "FROM feedback f " +
           "WHERE (:year IS NULL OR f.year = :year) " +
           "AND (:term IS NULL OR f.term = :term) " +
           "GROUP BY f.branch", nativeQuery = true)
    List<Object[]> getBranchAnalysis(@Param("year") String year, @Param("term") String term);
    
    @Query(value = "SELECT CONCAT(f.year, '-', f.term) as yearTerm, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q1') AS double precision)) as q1, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q2') AS double precision)) as q2, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q3') AS double precision)) as q3, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q4') AS double precision)) as q4, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q5') AS double precision)) as q5, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q6') AS double precision)) as q6, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q7') AS double precision)) as q7, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q8') AS double precision)) as q8, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q9') AS double precision)) as q9, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q10') AS double precision)) as q10, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q11') AS double precision)) as q11, " +
           "AVG(CAST((f.ratings\\:\\:json->>'Q12') AS double precision)) as q12, " +
           "COUNT(f) as count " +
           "FROM feedback f " +
           "GROUP BY CONCAT(f.year, '-', f.term)", nativeQuery = true)
    List<Object[]> getTermYearAnalysis();
}
