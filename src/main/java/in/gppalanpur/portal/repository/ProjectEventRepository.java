package in.gppalanpur.portal.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.ProjectEvent;
import in.gppalanpur.portal.entity.ProjectEvent.Status;

@Repository
public interface ProjectEventRepository extends JpaRepository<ProjectEvent, Long> {
    
    List<ProjectEvent> findByIsActive(boolean isActive);
    
    List<ProjectEvent> findByStatus(Status status);
    
    List<ProjectEvent> findByAcademicYear(String academicYear);
    
    List<ProjectEvent> findByEventDateBetween(LocalDate start, LocalDate end);
    
    List<ProjectEvent> findByDepartmentsContaining(Department department);
    
    @Query("SELECT e FROM ProjectEvent e WHERE e.isActive = true AND e.registrationStartDate <= CURRENT_DATE AND e.registrationEndDate >= CURRENT_DATE")
    List<ProjectEvent> findActiveEvents();
    
    Optional<ProjectEvent> findByNameAndAcademicYear(String name, String academicYear);
}