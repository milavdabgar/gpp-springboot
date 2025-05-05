package in.gppalanpur.portal.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.project.CreateProjectRequest;
import in.gppalanpur.portal.dto.project.EvaluateProjectRequest;
import in.gppalanpur.portal.dto.project.ProjectDetailsResponse;
import in.gppalanpur.portal.dto.project.ProjectImportResult;
import in.gppalanpur.portal.dto.project.ProjectResponse;
import in.gppalanpur.portal.dto.project.UpdateProjectRequest;

/**
 * Service interface for Project operations.
 */
public interface ProjectService {
    
    /**
     * Get all projects with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of ProjectResponse objects
     */
    Page<ProjectResponse> getAllProjects(Pageable pageable);
    
    /**
     * Get a project by ID.
     * 
     * @param id Project ID
     * @return ProjectResponse object
     */
    ProjectResponse getProject(Long id);
    
    /**
     * Get detailed project information by ID.
     * 
     * @param id Project ID
     * @return ProjectDetailsResponse object
     */
    ProjectDetailsResponse getProjectDetails(Long id);
    
    /**
     * Create a new project.
     * 
     * @param request Project creation request
     * @param creatorId ID of the user creating the project
     * @return ProjectResponse object
     */
    ProjectResponse createProject(CreateProjectRequest request, Long creatorId);
    
    /**
     * Update an existing project.
     * 
     * @param id Project ID
     * @param request Project update request
     * @param updaterId ID of the user updating the project
     * @return ProjectResponse object
     */
    ProjectResponse updateProject(Long id, UpdateProjectRequest request, Long updaterId);
    
    /**
     * Delete a project.
     * 
     * @param id Project ID
     */
    void deleteProject(Long id);
    
    /**
     * Get projects created by a specific user.
     * 
     * @param creatorId User ID
     * @param pageable Pagination parameters
     * @return Page of ProjectResponse objects
     */
    Page<ProjectResponse> getProjectsByCreator(Long creatorId, Pageable pageable);
    
    /**
     * Get projects for a specific department.
     * 
     * @param departmentId Department ID
     * @param pageable Pagination parameters
     * @return Page of ProjectResponse objects
     */
    Page<ProjectResponse> getProjectsByDepartment(Long departmentId, Pageable pageable);
    
    /**
     * Get projects for a specific event.
     * 
     * @param eventId Event ID
     * @param pageable Pagination parameters
     * @return Page of ProjectResponse objects
     */
    Page<ProjectResponse> getProjectsByEvent(Long eventId, Pageable pageable);
    
    /**
     * Get projects for a specific team.
     * 
     * @param teamId Team ID
     * @return List of ProjectResponse objects
     */
    List<ProjectResponse> getProjectsByTeam(Long teamId);
    
    /**
     * Get project statistics.
     * 
     * @return Map of statistics
     */
    Map<String, Object> getProjectStatistics();
    
    /**
     * Get project counts by category.
     * 
     * @param eventId Optional event ID filter
     * @return Map of category counts
     */
    Map<String, Long> getProjectCountsByCategory(Long eventId);
    
    /**
     * Import projects from a CSV file.
     * 
     * @param file CSV file
     * @return Import result
     */
    ProjectImportResult importProjects(MultipartFile file);
    
    /**
     * Export projects to a CSV file.
     * 
     * @return CSV file as byte array
     */
    byte[] exportProjects();
    
    /**
     * Evaluate a project by department jury.
     * 
     * @param id Project ID
     * @param request Evaluation request
     * @param juryId ID of the jury member
     * @return Updated ProjectResponse
     */
    ProjectResponse evaluateProjectByDepartment(Long id, EvaluateProjectRequest request, Long juryId);
    
    /**
     * Evaluate a project by central jury.
     * 
     * @param id Project ID
     * @param request Evaluation request
     * @param juryId ID of the jury member
     * @return Updated ProjectResponse
     */
    ProjectResponse evaluateProjectByCentral(Long id, EvaluateProjectRequest request, Long juryId);
    
    /**
     * Get projects assigned to a specific jury.
     * 
     * @param juryId Jury ID
     * @return List of ProjectResponse objects
     */
    List<ProjectResponse> getProjectsForJury(Long juryId);
    
    /**
     * Get winners for a specific event.
     * 
     * @param eventId Event ID
     * @return List of ProjectResponse objects
     */
    List<ProjectResponse> getEventWinners(Long eventId);
}
