package in.gppalanpur.portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.ProjectEvent;
import in.gppalanpur.portal.entity.ProjectTeam;
import in.gppalanpur.portal.entity.User;

@Repository
public interface ProjectTeamRepository extends JpaRepository<ProjectTeam, Long> {
    
    List<ProjectTeam> findByDepartment(Department department);
    
    List<ProjectTeam> findByEvent(ProjectEvent event);
    
    @Query("SELECT t FROM ProjectTeam t JOIN t.members m WHERE m.user = :user")
    List<ProjectTeam> findByMember(@Param("user") User user);
    
    @Query("SELECT DISTINCT t FROM ProjectTeam t JOIN t.members m WHERE m.user = :user AND m.isLeader = true")
    List<ProjectTeam> findByLeader(@Param("user") User user);
    
    Page<ProjectTeam> findByDepartmentAndEvent(Department department, ProjectEvent event, Pageable pageable);
    
    @Query("SELECT t FROM ProjectTeam t JOIN t.members m WHERE t.department = :department AND m.user = :user")
    List<ProjectTeam> findByDepartmentAndMember(@Param("department") Department department, @Param("user") User user);
}