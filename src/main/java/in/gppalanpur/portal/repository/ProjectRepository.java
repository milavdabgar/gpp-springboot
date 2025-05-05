package in.gppalanpur.portal.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Event;
import in.gppalanpur.portal.entity.Project;
import in.gppalanpur.portal.entity.Project.Status;
import in.gppalanpur.portal.entity.Team;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    
    List<Project> findByStatus(Status status);
    
    List<Project> findByDepartment(Department department);
    Page<Project> findByDepartment(Department department, Pageable pageable);
    
    List<Project> findByTeam(Team team);
    
    List<Project> findByEvent(Event event);
    Page<Project> findByEvent(Event event, Pageable pageable);
    
    List<Project> findByCategory(String category);
    
    Page<Project> findByCreatedById(Long creatorId, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.department = :department AND p.status = :status")
    List<Project> findByDepartmentAndStatus(@Param("department") Department department, @Param("status") Status status);
    
    @Query("SELECT p FROM Project p WHERE p.event = :event AND p.status = :status")
    List<Project> findByEventAndStatus(@Param("event") Event event, @Param("status") Status status);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.deptEvaluation.completed = :completed")
    long countByDeptEvaluationCompleted(@Param("completed") boolean completed);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.centralEvaluation.completed = :completed")
    long countByCentralEvaluationCompleted(@Param("completed") boolean completed);
    
    @Query("SELECT p FROM Project p WHERE p.deptEvaluation.juryId = :juryId")
    List<Project> findByDeptJury(@Param("juryId") Long juryId);
    
    @Query("SELECT p FROM Project p WHERE p.centralEvaluation.juryId = :juryId")
    List<Project> findByCentralJury(@Param("juryId") Long juryId);
    
    Page<Project> findByEventAndDepartment(Event event, Department department, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.event = :event")
    Long countByEvent(@Param("event") Event event);
    
    @Query("SELECT p.category as category, COUNT(p) as count FROM Project p GROUP BY p.category")
    Map<String, Long> countByCategory();
    
    @Query("SELECT p.category as category, COUNT(p) as count FROM Project p WHERE p.event = :event GROUP BY p.category")
    Map<String, Long> countByCategoryForEvent(@Param("event") Event event);
    
    @Query("SELECT d.name as department, COUNT(p) as count FROM Project p JOIN p.department d GROUP BY d.name")
    Map<String, Long> countByDepartment();
    
    @Query("SELECT e.name as event, COUNT(p) as count FROM Project p JOIN p.event e GROUP BY e.name")
    Map<String, Long> countByEvent();
    
    @Query("SELECT p FROM Project p WHERE p.event = :event AND p.centralEvaluation.completed = true ORDER BY p.centralEvaluation.score DESC")
    List<Project> findByEventAndCentralEvaluationCompletedOrderByScoreDesc(@Param("event") Event event, @Param("completed") boolean completed);

    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.department = :department")
    Long countByDepartment(@Param("department") Department department);
}