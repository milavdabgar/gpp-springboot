package in.gppalanpur.portal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Project;
import in.gppalanpur.portal.entity.ProjectEvent;
import in.gppalanpur.portal.entity.ProjectLocation;

@Repository
public interface ProjectLocationRepository extends JpaRepository<ProjectLocation, Long> {
    
    Optional<ProjectLocation> findByLocationId(String locationId);
    
    List<ProjectLocation> findBySection(String section);
    
    List<ProjectLocation> findBySectionAndPosition(String section, Integer position);
    
    List<ProjectLocation> findByDepartment(Department department);
    
    List<ProjectLocation> findByEvent(ProjectEvent event);
    
    List<ProjectLocation> findByIsAssigned(boolean isAssigned);
    
    List<ProjectLocation> findByProject(Project project);
    
    Page<ProjectLocation> findByDepartmentAndEvent(Department department, ProjectEvent event, Pageable pageable);
    
    List<ProjectLocation> findByEventAndSection(ProjectEvent event, String section);
    
    boolean existsByLocationId(String locationId);
}