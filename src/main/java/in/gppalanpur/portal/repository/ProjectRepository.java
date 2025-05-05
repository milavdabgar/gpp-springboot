package in.gppalanpur.portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Project;
import in.gppalanpur.portal.entity.Project.Status;
import in.gppalanpur.portal.entity.ProjectEvent;
import in.gppalanpur.portal.entity.ProjectTeam;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    
    List<Project> findByStatus(Status status);
    
    List<Project> findByDepartment(Department department);
    
    List<Project> findByTeam(ProjectTeam team);
    
    List<Project> findByEvent(ProjectEvent event);
    
    List<Project> findByCategory(String category);
    
    @Query("SELECT p FROM Project p WHERE p.department = :department AND p.status = :status")
    List<Project> findByDepartmentAndStatus(@Param("department") Department department, @Param("status") Status status);
    
    @Query("SELECT p FROM Project p WHERE p.event = :event AND p.status = :status")
    List<Project> findByEventAndStatus(@Param("event") ProjectEvent event, @Param("status") Status status);
    
    @Query("SELECT p FROM Project p WHERE p.deptEvaluation.completed = :completed")
    List<Project> findByDeptEvaluationCompleted(@Param("completed") boolean completed);
    
    @Query("SELECT p FROM Project p WHERE p.centralEvaluation.completed = :completed")
    List<Project> findByCentralEvaluationCompleted(@Param("completed") boolean completed);
    
    @Query("SELECT p FROM Project p WHERE p.deptEvaluation.juryId = :juryId")
    List<Project> findByDeptJury(@Param("juryId") Long juryId);
    
    @Query("SELECT p FROM Project p WHERE p.centralEvaluation.juryId = :juryId")
    List<Project> findByCentralJury(@Param("juryId") Long juryId);
    
    Page<Project> findByEventAndDepartment(ProjectEvent event, Department department, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.event = :event")
    Long countByEvent(@Param("event") ProjectEvent event);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.department = :department")
    Long countByDepartment(@Param("department") Department department);
    
    @Query("SELECT p.category, COUNT(p) FROM Project p WHERE p.event = :event GROUP BY p.category")
    List<Object[]> countByEventGroupByCategory(@Param("event") ProjectEvent event);
}