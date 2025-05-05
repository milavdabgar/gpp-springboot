package in.gppalanpur.portal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.SemesterStatus;
import in.gppalanpur.portal.entity.Student;
import in.gppalanpur.portal.entity.User;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    
    Optional<Student> findByEnrollmentNo(String enrollmentNo);
    
    Optional<Student> findByInstitutionalEmail(String institutionalEmail);
    
    Optional<Student> findByUser(User user);
    
    List<Student> findByDepartment(Department department);
    
    @Query("SELECT s FROM Student s WHERE s.department = :department AND s.semester = :semester")
    List<Student> findByDepartmentAndSemester(@Param("department") Department department, @Param("semester") Integer semester);
    
    @Query("SELECT s FROM Student s WHERE s.firstName LIKE %:search% OR s.lastName LIKE %:search% OR s.enrollmentNo LIKE %:search%")
    Page<Student> searchStudents(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT DISTINCT s.batch FROM Student s ORDER BY s.batch DESC")
    List<String> findDistinctBatches();

    @Query("SELECT s FROM Student s WHERE s.department = :department AND s.semesterStatus.sem1 = :status")
    List<Student> findBySemester1Status(@Param("department") Department department, @Param("status") SemesterStatus.Status status);
    
    @Query("SELECT s FROM Student s WHERE s.department = :department AND s.semesterStatus.sem2 = :status")
    List<Student> findBySemester2Status(@Param("department") Department department, @Param("status") SemesterStatus.Status status);
    
    @Query("SELECT s FROM Student s WHERE s.department = :department AND s.semesterStatus.sem3 = :status")
    List<Student> findBySemester3Status(@Param("department") Department department, @Param("status") SemesterStatus.Status status);
    
    @Query("SELECT s FROM Student s WHERE s.department = :department AND s.semesterStatus.sem4 = :status")
    List<Student> findBySemester4Status(@Param("department") Department department, @Param("status") SemesterStatus.Status status);
    
    @Query("SELECT s FROM Student s WHERE s.department = :department AND s.semesterStatus.sem5 = :status")
    List<Student> findBySemester5Status(@Param("department") Department department, @Param("status") SemesterStatus.Status status);
    
    @Query("SELECT s FROM Student s WHERE s.department = :department AND s.semesterStatus.sem6 = :status")
    List<Student> findBySemester6Status(@Param("department") Department department, @Param("status") SemesterStatus.Status status);
}