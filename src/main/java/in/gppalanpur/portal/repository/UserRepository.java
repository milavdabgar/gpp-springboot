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
import in.gppalanpur.portal.entity.Student;
import in.gppalanpur.portal.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:search% OR u.email LIKE %:search%")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
    
    // Custom method to find users by role using Java filtering instead of JPQL
    default List<User> findByRole(String role) {
        return findAll().stream()
                .filter(user -> user.getRoles() != null && user.getRoles().contains(role))
                .toList();
    }
    
    Page<User> findByDepartment(Department department, Pageable pageable);
    
    // Custom method to find users by department and role using Java filtering
    default List<User> findByDepartmentAndRole(Department department, String role) {
        return findByDepartment(department).stream()
                .filter(user -> user.getRoles() != null && user.getRoles().contains(role))
                .toList();
    }
    
    // Helper method to find all users by department without pagination
    @Query("SELECT u FROM User u WHERE u.department = :department")
    List<User> findByDepartment(@Param("department") Department department);
    
    /**
     * Find a student by user ID.
     * 
     * @param userId The ID of the user
     * @return Optional containing the student if found
     */
    @Query("SELECT s FROM Student s WHERE s.user.id = :userId")
    Optional<Student> findStudentByUserId(@Param("userId") Long userId);
    
    /**
     * Find the HOD (Head of Department) for a specific department.
     * 
     * @param departmentId The ID of the department
     * @return Optional containing the HOD if found
     */
    default Optional<User> findHodByDepartmentId(Long departmentId) {
        return findAll().stream()
                .filter(user -> user.getRoles() != null && 
                        user.getRoles().contains("ROLE_hod") && 
                        user.getDepartment() != null && 
                        user.getDepartment().getId().equals(departmentId))
                .findFirst();
    }
}