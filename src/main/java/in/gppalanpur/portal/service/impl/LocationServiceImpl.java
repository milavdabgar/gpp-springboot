package in.gppalanpur.portal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gppalanpur.portal.dto.location.CreateLocationRequest;
import in.gppalanpur.portal.dto.location.LocationResponse;
import in.gppalanpur.portal.dto.location.UpdateLocationRequest;
import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Event;
import in.gppalanpur.portal.entity.Location;
import in.gppalanpur.portal.entity.Project;
import in.gppalanpur.portal.entity.User;
import in.gppalanpur.portal.exception.ResourceNotFoundException;
import in.gppalanpur.portal.repository.DepartmentRepository;
import in.gppalanpur.portal.repository.EventRepository;
import in.gppalanpur.portal.repository.LocationRepository;
import in.gppalanpur.portal.repository.ProjectRepository;
import in.gppalanpur.portal.repository.UserRepository;
import in.gppalanpur.portal.service.LocationService;

/**
 * Implementation of LocationService interface.
 */
@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private EventRepository eventRepository;

    @Override
    @Transactional
    public LocationResponse createLocation(CreateLocationRequest request, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
        
        Location location = Location.builder()
                .name(request.getName())
                .description(request.getDescription())
                .section(request.getSection())
                .building(request.getBuilding())
                .floor(request.getFloor())
                .room(request.getRoom())
                .capacity(request.getCapacity())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .department(department)
                .createdBy(creator)
                .updatedBy(creator)
                .build();
        
        location = locationRepository.save(location);
        
        return convertToDto(location);
    }

    @Override
    @Transactional
    public LocationResponse updateLocation(Long id, UpdateLocationRequest request, Long userId) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (request.getName() != null) {
            location.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            location.setDescription(request.getDescription());
        }
        
        if (request.getSection() != null) {
            location.setSection(request.getSection());
        }
        
        if (request.getBuilding() != null) {
            location.setBuilding(request.getBuilding());
        }
        
        if (request.getFloor() != null) {
            location.setFloor(request.getFloor());
        }
        
        if (request.getRoom() != null) {
            location.setRoom(request.getRoom());
        }
        
        if (request.getCapacity() != null) {
            location.setCapacity(request.getCapacity());
        }
        
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
            location.setDepartment(department);
        }
        
        if (request.getIsActive() != null) {
            location.setActive(request.getIsActive());
        }
        
        location.setUpdatedBy(updater);
        location = locationRepository.save(location);
        
        return convertToDto(location);
    }

    @Override
    public LocationResponse getLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        return convertToDto(location);
    }

    @Override
    public Page<LocationResponse> getAllLocations(Pageable pageable) {
        return locationRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<LocationResponse> getActiveLocations(Pageable pageable) {
        return locationRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<LocationResponse> getLocationsByDepartment(Long departmentId, Pageable pageable) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
        
        return locationRepository.findByDepartment(department, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<LocationResponse> getLocationsBySection(String section, Pageable pageable) {
        return locationRepository.findBySection(section, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<LocationResponse> getLocationsByEvent(Long eventId, Pageable pageable) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        // Find projects associated with this event
        List<Project> projects = projectRepository.findByEvent(event);
        
        // Find locations associated with these projects
        // This is a simplified approach; in a real implementation, you might need a more efficient query
        Page<Location> locations = locationRepository.findAll(pageable);
        
        return locations
                .map(location -> {
                    LocationResponse response = convertToDto(location);
                    
                    // Check if any project in this event is assigned to this location
                    for (Project project : projects) {
                        if (project.getLocation() != null && project.getLocation().getId().equals(location.getId())) {
                            response.setProjectId(project.getId());
                            response.setProjectName(project.getTitle());
                            if (project.getTeam() != null) {
                                response.setProjectTeamName(project.getTeam().getName());
                            }
                            break;
                        }
                    }
                    
                    return response;
                });
    }

    @Override
    @Transactional
    public LocationResponse assignProjectToLocation(Long locationId, Long projectId, Long userId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Unassign the project from its current location if it has one
        if (project.getLocation() != null && !project.getLocation().getId().equals(locationId)) {
            List<Project> currentProjects = projectRepository.findByLocation(project.getLocation());
            if (!currentProjects.isEmpty()) {
                for (Project currentProject : currentProjects) {
                    currentProject.setLocation(null);
                    projectRepository.save(currentProject);
                }
            }
        }
        
        // Assign the project to the new location
        project.setLocation(location);
        project.setUpdatedBy(updater);
        projectRepository.save(project);
        
        location.setUpdatedBy(updater);
        location = locationRepository.save(location);
        
        LocationResponse response = convertToDto(location);
        response.setProjectId(project.getId());
        response.setProjectName(project.getTitle());
        if (project.getTeam() != null) {
            response.setProjectTeamName(project.getTeam().getName());
        }
        
        return response;
    }

    @Override
    @Transactional
    public LocationResponse unassignProjectFromLocation(Long locationId, Long userId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));
        
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Find the projects assigned to this location
        List<Project> projects = projectRepository.findByLocation(location);
        if (!projects.isEmpty()) {
            for (Project project : projects) {
                project.setLocation(null);
                project.setUpdatedBy(updater);
                projectRepository.save(project);
            }
        }
        
        location.setUpdatedBy(updater);
        location = locationRepository.save(location);
        
        return convertToDto(location);
    }

    @Override
    public Map<String, Object> getLocationStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Total locations
        long totalLocations = locationRepository.count();
        statistics.put("totalLocations", totalLocations);
        
        // Active locations
        long activeLocations = locationRepository.findByIsActiveTrue().size();
        statistics.put("activeLocations", activeLocations);
        
        // Locations by department
        List<Department> departments = departmentRepository.findAll();
        Map<String, Long> locationsByDepartment = new HashMap<>();
        
        for (Department department : departments) {
            long count = locationRepository.countByDepartment(department);
            locationsByDepartment.put(department.getName(), count);
        }
        
        statistics.put("locationsByDepartment", locationsByDepartment);
        
        // Locations by section
        List<Location> locations = locationRepository.findAll();
        Map<String, Long> locationsBySection = new HashMap<>();
        
        for (Location location : locations) {
            if (location.getSection() != null) {
                String section = location.getSection();
                locationsBySection.put(section, locationsBySection.getOrDefault(section, 0L) + 1);
            }
        }
        
        statistics.put("locationsBySection", locationsBySection);
        
        return statistics;
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        
        // Unassign any projects assigned to this location
        List<Project> projects = projectRepository.findByLocation(location);
        if (!projects.isEmpty()) {
            for (Project project : projects) {
                project.setLocation(null);
                projectRepository.save(project);
            }
        }
        
        locationRepository.delete(location);
    }

    @Override
    public LocationResponse convertToDto(Location location) {
        if (location == null) {
            return null;
        }
        
        // Find any project assigned to this location
        List<Project> projects = projectRepository.findByLocation(location);
        Project project = projects.isEmpty() ? null : projects.get(0);
        
        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .description(location.getDescription())
                .section(location.getSection())
                .building(location.getBuilding())
                .floor(location.getFloor())
                .room(location.getRoom())
                .capacity(location.getCapacity())
                .isActive(location.isActive())
                .departmentId(location.getDepartment() != null ? location.getDepartment().getId() : null)
                .departmentName(location.getDepartment() != null ? location.getDepartment().getName() : null)
                .projectId(project != null ? project.getId() : null)
                .projectName(project != null ? project.getTitle() : null)
                .projectTeamName(project != null && project.getTeam() != null ? project.getTeam().getName() : null)
                .createdById(location.getCreatedBy() != null ? location.getCreatedBy().getId() : null)
                .createdByName(location.getCreatedBy() != null ? location.getCreatedBy().getName() : null)
                .updatedById(location.getUpdatedBy() != null ? location.getUpdatedBy().getId() : null)
                .updatedByName(location.getUpdatedBy() != null ? location.getUpdatedBy().getName() : null)
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }

    @Override
    public List<Location> findActiveLocationsByDepartment(Department department) {
        return locationRepository.findByDepartmentAndIsActiveTrue(department);
    }

    @Override
    public List<Location> findActiveLocationsByEvent(Event event) {
        // Find projects associated with this event
        List<Project> projects = projectRepository.findByEvent(event);
        
        // Find locations associated with these projects
        // This is a simplified approach; in a real implementation, you might need a more efficient query
        List<Location> allActiveLocations = locationRepository.findByIsActiveTrue();
        
        return allActiveLocations.stream()
                .filter(location -> {
                    for (Project project : projects) {
                        if (project.getLocation() != null && project.getLocation().getId().equals(location.getId())) {
                            return true;
                        }
                    }
                    return false;
                })
                .toList();
    }
}
