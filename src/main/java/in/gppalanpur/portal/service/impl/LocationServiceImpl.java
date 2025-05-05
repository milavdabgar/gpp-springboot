package in.gppalanpur.portal.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.location.CreateLocationBatchRequest;
import in.gppalanpur.portal.dto.location.CreateLocationRequest;
import in.gppalanpur.portal.dto.location.LocationImportResult;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of LocationService interface.
 */
@Service
@Slf4j
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
        
        Event event = null;
        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + request.getEventId()));
        }
        
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
                .event(event)
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
        
        if (request.getIsActive() != null) {
            location.setIsActive(request.getIsActive());
        }
        
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
            location.setDepartment(department);
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
    public Page<LocationResponse> getAllLocations(Long departmentId, Long eventId, String section, Boolean isAssigned, Pageable pageable) {
        // Build specifications based on filters
        Specification<Location> spec = Specification.where(null);
        
        if (departmentId != null) {
            spec = spec.and((root, query, cb) -> {
                return cb.equal(root.get("department").get("id"), departmentId);
            });
        }
        
        if (eventId != null) {
            spec = spec.and((root, query, cb) -> {
                return cb.equal(root.get("event").get("id"), eventId);
            });
        }
        
        if (section != null && !section.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                return cb.equal(root.get("section"), section);
            });
        }
        
        if (isAssigned != null) {
            spec = spec.and((root, query, cb) -> {
                if (isAssigned) {
                    return cb.isNotNull(root.get("project"));
                } else {
                    return cb.isNull(root.get("project"));
                }
            });
        }
        
        Page<Location> locationsPage = locationRepository.findAll(spec, pageable);
        return locationsPage.map(this::convertToDto);
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
        
        return locationRepository.findByEvent(event, pageable)
                .map(this::convertToDto);
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
        
        location.setProject(project);
        location.setUpdatedBy(updater);
        
        location = locationRepository.save(location);
        
        return convertToDto(location);
    }

    @Override
    @Transactional
    public LocationResponse unassignProjectFromLocation(Long locationId, Long userId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));
        
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        location.setProject(null);
        location.setUpdatedBy(updater);
        
        location = locationRepository.save(location);
        
        return convertToDto(location);
    }

    @Override
    public Map<String, Object> getLocationStatistics() {
        long totalLocations = locationRepository.count();
        long activeLocations = locationRepository.countByIsActiveTrue();
        long assignedLocations = locationRepository.countByProjectIsNotNull();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalLocations", totalLocations);
        statistics.put("activeLocations", activeLocations);
        statistics.put("assignedLocations", assignedLocations);
        statistics.put("availableLocations", activeLocations - assignedLocations);
        
        // Locations by department
        List<Department> departments = departmentRepository.findAll();
        Map<String, Long> locationsByDepartment = new HashMap<>();
        
        for (Department department : departments) {
            long count = locationRepository.countByDepartment(department);
            locationsByDepartment.put(department.getName(), count);
        }
        
        statistics.put("byDepartment", locationsByDepartment);
        
        return statistics;
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        
        locationRepository.delete(location);
    }

    @Override
    public LocationResponse convertToDto(Location location) {
        if (location == null) {
            return null;
        }
        
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
                .projectId(location.getProject() != null ? location.getProject().getId() : null)
                .projectName(location.getProject() != null ? location.getProject().getTitle() : null)
                .eventId(location.getEvent() != null ? location.getEvent().getId() : null)
                .eventName(location.getEvent() != null ? location.getEvent().getName() : null)
                .createdById(location.getCreatedBy() != null ? location.getCreatedBy().getId() : null)
                .createdByName(location.getCreatedBy() != null ? location.getCreatedBy().getName() : null)
                .updatedById(location.getUpdatedBy() != null ? location.getUpdatedBy().getId() : null)
                .updatedByName(location.getUpdatedBy() != null ? location.getUpdatedBy().getName() : null)
                .build();
    }

    @Override
    public List<Location> findActiveLocationsByDepartment(Department department) {
        return locationRepository.findByDepartmentAndIsActiveTrue(department);
    }

    @Override
    public List<Location> findActiveLocationsByEvent(Event event) {
        return locationRepository.findByEventAndIsActiveTrue(event);
    }

    @Override
    @Transactional
    public LocationImportResult importLocationsFromCsv(MultipartFile file, Long userId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Location> importedLocations = new ArrayList<>();
        List<Map<String, String>> errors = new ArrayList<>();
        
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {
            
            for (CSVRecord record : csvParser) {
                try {
                    // Required fields
                    String name = record.get("name");
                    String section = record.get("section");
                    String building = record.get("building");
                    String floor = record.get("floor");
                    String room = record.get("room");
                    int capacity = Integer.parseInt(record.get("capacity"));
                    Long departmentId = Long.parseLong(record.get("departmentId"));
                    
                    // Optional fields
                    String description = record.isMapped("description") ? record.get("description") : null;
                    boolean isActive = record.isMapped("isActive") ? Boolean.parseBoolean(record.get("isActive")) : true;
                    
                    // Validate department
                    Department department = departmentRepository.findById(departmentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
                    
                    // Create location
                    Location location = Location.builder()
                            .name(name)
                            .description(description)
                            .section(section)
                            .building(building)
                            .floor(floor)
                            .room(room)
                            .capacity(capacity)
                            .isActive(isActive)
                            .department(department)
                            .createdBy(creator)
                            .updatedBy(creator)
                            .build();
                    
                    location = locationRepository.save(location);
                    importedLocations.add(location);
                    
                } catch (Exception e) {
                    Map<String, String> error = new HashMap<>();
                    error.put("line", String.valueOf(record.getRecordNumber()));
                    error.put("error", e.getMessage());
                    errors.add(error);
                    log.error("Error importing location at line {}: {}", record.getRecordNumber(), e.getMessage());
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }
        
        return LocationImportResult.builder()
                .successCount(importedLocations.size())
                .errorCount(errors.size())
                .totalProcessed(importedLocations.size() + errors.size())
                .errors(errors)
                .build();
    }

    @Override
    public byte[] exportLocationsToCsv() {
        List<Location> locations = locationRepository.findAll();
        
        String[] headers = {
                "id", "name", "description", "section", "building", "floor", "room", 
                "capacity", "departmentId", "departmentName", "isActive"
        };
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = CSVFormat.DEFAULT.builder()
                     .setHeader(headers)
                     .build()
                     .print(writer)) {
            
            for (Location location : locations) {
                csvPrinter.printRecord(
                        location.getId(),
                        location.getName(),
                        location.getDescription(),
                        location.getSection(),
                        location.getBuilding(),
                        location.getFloor(),
                        location.getRoom(),
                        location.getCapacity(),
                        location.getDepartment() != null ? location.getDepartment().getId() : null,
                        location.getDepartment() != null ? location.getDepartment().getName() : null,
                        location.isActive()
                );
            }
            
            csvPrinter.flush();
            return out.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to export locations to CSV: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<LocationResponse> createLocationBatch(CreateLocationBatchRequest request, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Location> createdLocations = new ArrayList<>();
        
        for (CreateLocationRequest locationRequest : request.getLocations()) {
            Department department = departmentRepository.findById(locationRequest.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + locationRequest.getDepartmentId()));
            
            Location location = Location.builder()
                    .name(locationRequest.getName())
                    .description(locationRequest.getDescription())
                    .section(locationRequest.getSection())
                    .building(locationRequest.getBuilding())
                    .floor(locationRequest.getFloor())
                    .room(locationRequest.getRoom())
                    .capacity(locationRequest.getCapacity())
                    .isActive(locationRequest.getIsActive() != null ? locationRequest.getIsActive() : true)
                    .department(department)
                    .createdBy(creator)
                    .updatedBy(creator)
                    .build();
            
            location = locationRepository.save(location);
            createdLocations.add(location);
        }
        
        return createdLocations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getLocationsByEventGroupedBySection(Long eventId) {
        // Check if event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        // Get all locations for the event
        List<Location> locations = locationRepository.findByEventAndIsActiveTrue(event);
        
        // Group locations by section
        Map<String, List<Location>> sectionMap = locations.stream()
                .collect(Collectors.groupingBy(Location::getSection));
        
        // Convert map to list of section objects with locations
        List<Map<String, Object>> sectionsList = new ArrayList<>();
        
        sectionMap.forEach((sectionName, sectionLocations) -> {
            Map<String, Object> sectionObj = new LinkedHashMap<>();
            sectionObj.put("section", sectionName);
            sectionObj.put("locations", sectionLocations.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
            sectionsList.add(sectionObj);
        });
        
        // Create result map with sections and statistics
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sections", sectionsList);
        result.put("totalLocations", locations.size());
        result.put("assignedLocations", locations.stream()
                .filter(loc -> loc.getProject() != null)
                .count());
        
        return result;
    }
}
