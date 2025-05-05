package in.gppalanpur.portal.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import in.gppalanpur.portal.dto.project.CreateProjectRequest;
import in.gppalanpur.portal.dto.project.EvaluateProjectRequest;
import in.gppalanpur.portal.dto.project.ProjectDetailsResponse;
import in.gppalanpur.portal.dto.project.ProjectEvaluationDto;
import in.gppalanpur.portal.dto.project.ProjectImportResult;
import in.gppalanpur.portal.dto.project.ProjectResponse;
import in.gppalanpur.portal.dto.project.TeamMemberDto;
import in.gppalanpur.portal.dto.project.UpdateProjectRequest;
import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Event;
import in.gppalanpur.portal.entity.Location;
import in.gppalanpur.portal.entity.Project;
import in.gppalanpur.portal.entity.ProjectEvaluation;
import in.gppalanpur.portal.entity.ProjectRequirements;
import in.gppalanpur.portal.entity.Team;
import in.gppalanpur.portal.entity.User;
import in.gppalanpur.portal.repository.DepartmentRepository;
import in.gppalanpur.portal.repository.EventRepository;
import in.gppalanpur.portal.repository.LocationRepository;
import in.gppalanpur.portal.repository.ProjectRepository;
import in.gppalanpur.portal.repository.TeamRepository;
import in.gppalanpur.portal.repository.UserRepository;
import in.gppalanpur.portal.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    
    @Override
    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        Page<Project> projectsPage = projectRepository.findAll(pageable);
        List<ProjectResponse> projectResponses = projectsPage.getContent().stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(projectResponses, pageable, projectsPage.getTotalElements());
    }

    @Override
    public ProjectResponse getProject(Long id) {
        Project project = findProjectById(id);
        return mapToProjectResponse(project);
    }

    @Override
    public ProjectDetailsResponse getProjectDetails(Long id) {
        Project project = findProjectById(id);
        return mapToProjectDetailsResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, Long creatorId) {
        User creator = findUserById(creatorId);
        Department department = findDepartmentById(request.getDepartmentId());
        Team team = findTeamById(request.getTeamId());
        Event event = findEventById(request.getEventId());
        
        Location location = null;
        if (request.getLocationId() != null) {
            location = findLocationById(request.getLocationId());
        }
        
        ProjectRequirements requirements = new ProjectRequirements();
        if (request.getRequirements() != null) {
            // Map requirements from request
            requirements.setNeedsElectricity(request.getRequirements().getOrDefault("needsElectricity", false));
            requirements.setNeedsWater(request.getRequirements().getOrDefault("needsWater", false));
            requirements.setNeedsGas(request.getRequirements().getOrDefault("needsGas", false));
            requirements.setNeedsInternet(request.getRequirements().getOrDefault("needsInternet", false));
            requirements.setNeedsDisplay(request.getRequirements().getOrDefault("needsDisplay", false));
            requirements.setNeedsExtraSpace(request.getRequirements().getOrDefault("needsExtraSpace", false));
            requirements.setOtherRequirements(request.getRequirements().containsKey("otherRequirements") ? 
                request.getRequirements().get("otherRequirements").toString() : "");
        }
        
        Project project = Project.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .abstract_(request.getAbstract_())
                .status(request.getStatus() != null ? request.getStatus() : Project.Status.PENDING)
                .department(department)
                .team(team)
                .event(event)
                .location(location)
                .requirements(requirements)
                .guideName(request.getGuideName())
                .guideEmail(request.getGuideEmail())
                .guidePhone(request.getGuidePhone())
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .build();
        
        // Initialize evaluations
        ProjectEvaluation deptEvaluation = new ProjectEvaluation();
        deptEvaluation.setCompleted(false);
        
        ProjectEvaluation centralEvaluation = new ProjectEvaluation();
        centralEvaluation.setCompleted(false);
        
        project.setDeptEvaluation(deptEvaluation);
        project.setCentralEvaluation(centralEvaluation);
        
        Project savedProject = projectRepository.save(project);
        return mapToProjectResponse(savedProject);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request, Long updaterId) {
        Project project = findProjectById(id);
        User updater = findUserById(updaterId);
        
        // Update basic fields if provided
        if (request.getTitle() != null) {
            project.setTitle(request.getTitle());
        }
        
        if (request.getCategory() != null) {
            project.setCategory(request.getCategory());
        }
        
        if (request.getAbstract_() != null) {
            project.setAbstract_(request.getAbstract_());
        }
        
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        
        // Update relationships if provided
        if (request.getDepartmentId() != null) {
            Department department = findDepartmentById(request.getDepartmentId());
            project.setDepartment(department);
        }
        
        if (request.getTeamId() != null) {
            Team team = findTeamById(request.getTeamId());
            project.setTeam(team);
        }
        
        if (request.getEventId() != null) {
            Event event = findEventById(request.getEventId());
            project.setEvent(event);
        }
        
        if (request.getLocationId() != null) {
            Location location = findLocationById(request.getLocationId());
            project.setLocation(location);
        }
        
        // Update requirements if provided
        if (request.getRequirements() != null) {
            ProjectRequirements requirements = project.getRequirements();
            if (requirements == null) {
                requirements = new ProjectRequirements();
                project.setRequirements(requirements);
            }
            
            requirements.setNeedsElectricity(request.getRequirements().getOrDefault("needsElectricity", false));
            requirements.setNeedsWater(request.getRequirements().getOrDefault("needsWater", false));
            requirements.setNeedsGas(request.getRequirements().getOrDefault("needsGas", false));
            requirements.setNeedsInternet(request.getRequirements().getOrDefault("needsInternet", false));
            requirements.setNeedsDisplay(request.getRequirements().getOrDefault("needsDisplay", false));
            requirements.setNeedsExtraSpace(request.getRequirements().getOrDefault("needsExtraSpace", false));
        }
        
        // Update guide information if provided
        if (request.getGuideName() != null) {
            project.setGuideName(request.getGuideName());
        }
        
        if (request.getGuideEmail() != null) {
            project.setGuideEmail(request.getGuideEmail());
        }
        
        if (request.getGuidePhone() != null) {
            project.setGuidePhone(request.getGuidePhone());
        }
        
        // Update metadata
        project.setUpdatedBy(updater);
        project.setUpdatedAt(LocalDateTime.now());
        
        Project updatedProject = projectRepository.save(project);
        return mapToProjectResponse(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = findProjectById(id);
        projectRepository.delete(project);
    }

    @Override
    public Page<ProjectResponse> getProjectsByCreator(Long creatorId, Pageable pageable) {
        Page<Project> projectsPage = projectRepository.findByCreatedById(creatorId, pageable);
        List<ProjectResponse> projectResponses = projectsPage.getContent().stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(projectResponses, pageable, projectsPage.getTotalElements());
    }

    @Override
    public Page<ProjectResponse> getProjectsByDepartment(Long departmentId, Pageable pageable) {
        Department department = findDepartmentById(departmentId);
        Page<Project> projectsPage = projectRepository.findByDepartment(department, pageable);
        List<ProjectResponse> projectResponses = projectsPage.getContent().stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(projectResponses, pageable, projectsPage.getTotalElements());
    }

    @Override
    public Page<ProjectResponse> getProjectsByEvent(Long eventId, Pageable pageable) {
        Event event = findEventById(eventId);
        Page<Project> projectsPage = projectRepository.findByEvent(event, pageable);
        List<ProjectResponse> projectResponses = projectsPage.getContent().stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(projectResponses, pageable, projectsPage.getTotalElements());
    }

    @Override
    public List<ProjectResponse> getProjectsByTeam(Long teamId) {
        Team team = findTeamById(teamId);
        List<Project> projects = projectRepository.findByTeam(team);
        return projects.stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getProjectStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Total projects
        long totalProjects = projectRepository.count();
        statistics.put("totalProjects", totalProjects);
        
        // Projects by status
        Map<String, Long> projectsByStatus = new HashMap<>();
        for (Project.Status status : Project.Status.values()) {
            long count = projectRepository.findByStatus(status).size();
            projectsByStatus.put(status.name(), count);
        }
        statistics.put("projectsByStatus", projectsByStatus);
        
        // Projects by department
        Map<String, Long> projectsByDepartment = projectRepository.countByDepartment();
        statistics.put("projectsByDepartment", projectsByDepartment);
        
        // Projects by event
        Map<String, Long> projectsByEvent = projectRepository.countByEvent();
        statistics.put("projectsByEvent", projectsByEvent);
        
        // Projects by category
        Map<String, Long> projectsByCategory = projectRepository.countByCategory();
        statistics.put("projectsByCategory", projectsByCategory);
        
        // Evaluation statistics
        long evaluatedByDept = projectRepository.countByDeptEvaluationCompleted(true);
        long evaluatedByCentral = projectRepository.countByCentralEvaluationCompleted(true);
        statistics.put("evaluatedByDept", evaluatedByDept);
        statistics.put("evaluatedByCentral", evaluatedByCentral);
        
        return statistics;
    }

    @Override
    public Map<String, Long> getProjectCountsByCategory(Long eventId) {
        if (eventId != null) {
            Event event = findEventById(eventId);
            return projectRepository.countByCategoryForEvent(event);
        } else {
            return projectRepository.countByCategory();
        }
    }

    @Override
    public ProjectImportResult importProjects(MultipartFile file) {
        // Implementation for importing projects from CSV
        // This is a placeholder implementation
        ProjectImportResult result = new ProjectImportResult();
        result.setTotalProcessed(0);
        result.setSuccessCount(0);
        result.setFailureCount(0);
        result.setErrors(new ArrayList<>());
        
        log.warn("Project import not fully implemented yet");
        return result;
    }

    @Override
    public byte[] exportProjects() {
        // Implementation for exporting projects to CSV
        // This is a placeholder implementation
        log.warn("Project export not fully implemented yet");
        return new byte[0];
    }

    @Override
    @Transactional
    public ProjectResponse evaluateProjectByDepartment(Long id, EvaluateProjectRequest request, Long juryId) {
        Project project = findProjectById(id);
        User jury = findUserById(juryId);
        
        ProjectEvaluation evaluation = project.getDeptEvaluation();
        if (evaluation == null) {
            evaluation = new ProjectEvaluation();
            project.setDeptEvaluation(evaluation);
        }
        
        evaluation.setCompleted(true);
        evaluation.setScore(request.getScore());
        evaluation.setFeedback(request.getFeedback());
        evaluation.setJuryId(jury.getId());
        evaluation.setEvaluatedAt(LocalDateTime.now());
        
        Project updatedProject = projectRepository.save(project);
        return mapToProjectResponse(updatedProject);
    }

    @Override
    @Transactional
    public ProjectResponse evaluateProjectByCentral(Long id, EvaluateProjectRequest request, Long juryId) {
        Project project = findProjectById(id);
        User jury = findUserById(juryId);
        
        ProjectEvaluation evaluation = project.getCentralEvaluation();
        if (evaluation == null) {
            evaluation = new ProjectEvaluation();
            project.setCentralEvaluation(evaluation);
        }
        
        evaluation.setCompleted(true);
        evaluation.setScore(request.getScore());
        evaluation.setFeedback(request.getFeedback());
        evaluation.setJuryId(jury.getId());
        evaluation.setEvaluatedAt(LocalDateTime.now());
        
        Project updatedProject = projectRepository.save(project);
        return mapToProjectResponse(updatedProject);
    }

    @Override
    public List<ProjectResponse> getProjectsForJury(Long juryId) {
        List<Project> deptProjects = projectRepository.findByDeptJury(juryId);
        List<Project> centralProjects = projectRepository.findByCentralJury(juryId);
        
        // Combine both lists and remove duplicates
        List<Project> allProjects = new ArrayList<>(deptProjects);
        for (Project project : centralProjects) {
            if (!allProjects.contains(project)) {
                allProjects.add(project);
            }
        }
        
        return allProjects.stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectResponse> getEventWinners(Long eventId) {
        Event event = findEventById(eventId);
        
        // Get projects with central evaluation completed and sorted by score
        List<Project> projects = projectRepository.findByEventAndCentralEvaluationCompletedOrderByScoreDesc(event, true);
        
        return projects.stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
    }
    
    // Helper methods
    
    private Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id: " + id));
    }
    
    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found with id: " + id));
    }
    
    private Team findTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));
    }
    
    private Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id: " + id));
    }
    
    private Location findLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with id: " + id));
    }
    
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }
    
    private ProjectResponse mapToProjectResponse(Project project) {
        ProjectResponse response = ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .category(project.getCategory())
                .abstract_(project.getAbstract_())
                .status(project.getStatus())
                .build();
        
        // Add department info if available
        if (project.getDepartment() != null) {
            response.setDepartmentId(project.getDepartment().getId());
            response.setDepartmentName(project.getDepartment().getName());
        }
        
        // Add team info if available
        if (project.getTeam() != null) {
            response.setTeamId(project.getTeam().getId());
            response.setTeamName(project.getTeam().getName());
        }
        
        // Add event info if available
        if (project.getEvent() != null) {
            response.setEventId(project.getEvent().getId());
            response.setEventName(project.getEvent().getName());
        }
        
        // Add location info if available
        if (project.getLocation() != null) {
            response.setLocationId(project.getLocation().getId());
            response.setLocationName(project.getLocation().getName());
            response.setLocationSection(project.getLocation().getSection());
        }
        
        // Add requirements if available
        if (project.getRequirements() != null) {
            Map<String, Boolean> requirements = new HashMap<>();
            requirements.put("needsElectricity", project.getRequirements().getNeedsElectricity());
            requirements.put("needsWater", project.getRequirements().getNeedsWater());
            requirements.put("needsGas", project.getRequirements().getNeedsGas());
            requirements.put("needsInternet", project.getRequirements().getNeedsInternet());
            requirements.put("needsDisplay", project.getRequirements().getNeedsDisplay());
            requirements.put("needsExtraSpace", project.getRequirements().getNeedsExtraSpace());
            response.getRequirements().put("otherRequirements", Boolean.FALSE);
            // Store the otherRequirements as a separate field in the response
            if (project.getRequirements().getOtherRequirements() != null) {
                response.setOtherRequirements(project.getRequirements().getOtherRequirements());
            }
            response.setRequirements(requirements);
        }
        
        // Add guide info if available
        response.setGuideName(project.getGuideName());
        response.setGuideEmail(project.getGuideEmail());
        response.setGuidePhone(project.getGuidePhone());
        
        // Add evaluation info if available
        if (project.getDeptEvaluation() != null) {
            ProjectEvaluationDto deptEvaluation = mapToEvaluationDto(project.getDeptEvaluation());
            response.setDeptEvaluation(deptEvaluation);
        }
        
        if (project.getCentralEvaluation() != null) {
            ProjectEvaluationDto centralEvaluation = mapToEvaluationDto(project.getCentralEvaluation());
            response.setCentralEvaluation(centralEvaluation);
        }
        
        // Add metadata if available
        if (project.getCreatedBy() != null) {
            response.setCreatedById(project.getCreatedBy().getId());
            response.setCreatedByName(project.getCreatedBy().getName());
        }
        
        if (project.getUpdatedBy() != null) {
            response.setUpdatedById(project.getUpdatedBy().getId());
            response.setUpdatedByName(project.getUpdatedBy().getName());
        }
        
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        
        return response;
    }
    
    private ProjectDetailsResponse mapToProjectDetailsResponse(Project project) {
        ProjectResponse baseResponse = mapToProjectResponse(project);
        
        ProjectDetailsResponse detailsResponse = new ProjectDetailsResponse();
        // Copy all fields from baseResponse to detailsResponse
        detailsResponse.setId(baseResponse.getId());
        detailsResponse.setTitle(baseResponse.getTitle());
        detailsResponse.setCategory(baseResponse.getCategory());
        detailsResponse.setAbstract_(baseResponse.getAbstract_());
        detailsResponse.setStatus(baseResponse.getStatus());
        detailsResponse.setDepartmentId(baseResponse.getDepartmentId());
        detailsResponse.setDepartmentName(baseResponse.getDepartmentName());
        detailsResponse.setTeamId(baseResponse.getTeamId());
        detailsResponse.setTeamName(baseResponse.getTeamName());
        detailsResponse.setEventId(baseResponse.getEventId());
        detailsResponse.setEventName(baseResponse.getEventName());
        detailsResponse.setLocationId(baseResponse.getLocationId());
        detailsResponse.setLocationName(baseResponse.getLocationName());
        detailsResponse.setLocationSection(baseResponse.getLocationSection());
        detailsResponse.setRequirements(baseResponse.getRequirements());
        detailsResponse.setGuideName(baseResponse.getGuideName());
        detailsResponse.setGuideEmail(baseResponse.getGuideEmail());
        detailsResponse.setGuidePhone(baseResponse.getGuidePhone());
        detailsResponse.setDeptEvaluation(baseResponse.getDeptEvaluation());
        detailsResponse.setCentralEvaluation(baseResponse.getCentralEvaluation());
        detailsResponse.setCreatedById(baseResponse.getCreatedById());
        detailsResponse.setCreatedByName(baseResponse.getCreatedByName());
        detailsResponse.setUpdatedById(baseResponse.getUpdatedById());
        detailsResponse.setUpdatedByName(baseResponse.getUpdatedByName());
        detailsResponse.setCreatedAt(baseResponse.getCreatedAt());
        detailsResponse.setUpdatedAt(baseResponse.getUpdatedAt());
        
        // Add team members if available
        if (project.getTeam() != null && project.getTeam().getMembers() != null) {
            List<TeamMemberDto> teamMembers = project.getTeam().getMembers().stream()
                    .map(member -> {
                        TeamMemberDto dto = TeamMemberDto.builder()
                                .id(member.getId())
                                .name(member.getName())
                                .email(member.getEmail())
                                .build();
                        
                        // We don't have a phone field directly on User, so we'll leave it null
                        // In a real implementation, you might want to get this from a related entity
                        
                        // Check if member has a student record
                        if (member.getRoles() != null && member.getRoles().contains("ROLE_student")) {
                            // Find the student record for this user
                            userRepository.findStudentByUserId(member.getId()).ifPresent(student -> {
                                dto.setEnrollmentNumber(student.getEnrollmentNo());
                                dto.setSemester(student.getSemester());
                                
                                if (student.getDepartment() != null) {
                                    dto.setDepartmentName(student.getDepartment().getName());
                                }
                            });
                        }
                        
                        if (project.getTeam().getLeader() != null && 
                                project.getTeam().getLeader().getId().equals(member.getId())) {
                            dto.setIsLeader(true);
                        } else {
                            dto.setIsLeader(false);
                        }
                        
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            detailsResponse.setTeamMembers(teamMembers);
            
            // Add team leader info if available
            if (project.getTeam().getLeader() != null) {
                detailsResponse.setTeamLeaderName(project.getTeam().getLeader().getName());
                detailsResponse.setTeamLeaderEmail(project.getTeam().getLeader().getEmail());
            }
        }
        
        // Add department HOD info if available
        if (project.getDepartment() != null) {
            // Find the HOD for this department
            userRepository.findHodByDepartmentId(project.getDepartment().getId()).ifPresent(hod -> {
                detailsResponse.setDepartmentHodName(hod.getName());
                detailsResponse.setDepartmentHodEmail(hod.getEmail());
            });
        }
        
        // Add event details if available
        if (project.getEvent() != null) {
            detailsResponse.setEventDescription(project.getEvent().getDescription());
            
            if (project.getEvent().getStartDate() != null) {
                detailsResponse.setEventStartDate(project.getEvent().getStartDate().toString());
            }
            
            if (project.getEvent().getEndDate() != null) {
                detailsResponse.setEventEndDate(project.getEvent().getEndDate().toString());
            }
            
            detailsResponse.setEventResultsPublished(project.getEvent().isResultsPublished());
        }
        
        return detailsResponse;
    }
    
    private ProjectEvaluationDto mapToEvaluationDto(ProjectEvaluation evaluation) {
        if (evaluation == null) {
            return null;
        }
        
        ProjectEvaluationDto dto = ProjectEvaluationDto.builder()
                .completed(evaluation.getCompleted())
                .score(evaluation.getScore())
                .feedback(evaluation.getFeedback())
                .juryId(evaluation.getJuryId())
                .evaluatedAt(evaluation.getEvaluatedAt())
                .build();
        
        // Add jury name if available
        if (evaluation.getJuryId() != null) {
            userRepository.findById(evaluation.getJuryId())
                    .ifPresent(jury -> dto.setJuryName(jury.getName()));
        }
        
        return dto;
    }
}
