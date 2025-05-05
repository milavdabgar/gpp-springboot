package in.gppalanpur.portal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Faculty;
import in.gppalanpur.portal.entity.User;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    
    Optional<Faculty> findByUser(User user);
    
    Optional<Faculty> findByEmployeeId(String employeeId);
    
    List<Faculty> findByDepartment(Department department);
    
    List<Faculty> findByStatus(String status);
    
    List<Faculty> findByDepartmentAndStatus(Department department, String status);
}