package in.gppalanpur.portal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.User;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    Optional<Department> findByCode(String code);
    
    Optional<Department> findByName(String name);
    
    List<Department> findByIsActive(boolean isActive);
    
    List<Department> findByHodId(User hodId);
    
    boolean existsByCode(String code);
    
    boolean existsByName(String name);
}