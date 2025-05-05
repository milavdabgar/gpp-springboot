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

    // Custom methods to find students by semester status using Java filtering instead of JPQL
    default List<Student> findBySemester1Status(Department department, SemesterStatus.Status status) {
        return findByDepartment(department).stream()
                .filter(student -> student.getSemesterStatus() != null && student.getSemesterStatus().getSem1() == status)
                .toList();
    }
    
    default List<Student> findBySemester2Status(Department department, SemesterStatus.Status status) {
        return findByDepartment(department).stream()
                .filter(student -> student.getSemesterStatus() != null && student.getSemesterStatus().getSem2() == status)
                .toList();
    }
    
    default List<Student> findBySemester3Status(Department department, SemesterStatus.Status status) {
        return findByDepartment(department).stream()
                .filter(student -> student.getSemesterStatus() != null && student.getSemesterStatus().getSem3() == status)
                .toList();
    }
    
    default List<Student> findBySemester4Status(Department department, SemesterStatus.Status status) {
        return findByDepartment(department).stream()
                .filter(student -> student.getSemesterStatus() != null && student.getSemesterStatus().getSem4() == status)
                .toList();
    }
    
    default List<Student> findBySemester5Status(Department department, SemesterStatus.Status status) {
        return findByDepartment(department).stream()
                .filter(student -> student.getSemesterStatus() != null && student.getSemesterStatus().getSem5() == status)
                .toList();
    }
    
    default List<Student> findBySemester6Status(Department department, SemesterStatus.Status status) {
        return findByDepartment(department).stream()
                .filter(student -> student.getSemesterStatus() != null && student.getSemesterStatus().getSem6() == status)
                .toList();
    }
}