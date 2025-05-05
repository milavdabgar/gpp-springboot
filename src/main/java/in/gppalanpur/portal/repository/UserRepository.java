package in.gppalanpur.portal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:search% OR u.email LIKE %:search%")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    List<User> findByRole(@Param("role") String role);
    
    Page<User> findByDepartment(Department department, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.department = :department AND :role MEMBER OF u.roles")
    List<User> findByDepartmentAndRole(@Param("department") Department department, @Param("role") String role);
}