package in.gppalanpur.portal.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.location.CreateLocationBatchRequest;
import in.gppalanpur.portal.dto.location.CreateLocationRequest;
import in.gppalanpur.portal.dto.location.LocationImportResult;
import in.gppalanpur.portal.dto.location.LocationResponse;
import in.gppalanpur.portal.dto.location.UpdateLocationRequest;
import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Event;
import in.gppalanpur.portal.entity.Location;

/**
 * Service interface for Location operations.
 */
public interface LocationService {
    
    /**
     * Create a new location
     * 
     * @param request the location creation request
     * @param userId the ID of the user creating the location
     * @return the created location response
     */
    LocationResponse createLocation(CreateLocationRequest request, Long userId);
    
    /**
     * Update an existing location
     * 
     * @param id the location ID
     * @param request the location update request
     * @param userId the ID of the user updating the location
     * @return the updated location response
     */
    LocationResponse updateLocation(Long id, UpdateLocationRequest request, Long userId);
    
    /**
     * Get a location by ID
     * 
     * @param id the location ID
     * @return the location response
     */
    LocationResponse getLocation(Long id);
    
    /**
     * Get all locations with pagination
     * 
     * @param pageable pagination information
     * @return paginated list of location responses
     */
    Page<LocationResponse> getAllLocations(Pageable pageable);
    
    /**
     * Get active locations with pagination
     * 
     * @param pageable pagination information
     * @return paginated list of active location responses
     */
    Page<LocationResponse> getActiveLocations(Pageable pageable);
    
    /**
     * Get locations by department
     * 
     * @param departmentId the department ID
     * @param pageable pagination information
     * @return paginated list of location responses
     */
    Page<LocationResponse> getLocationsByDepartment(Long departmentId, Pageable pageable);
    
    /**
     * Get locations by section
     * 
     * @param section the section name
     * @param pageable pagination information
     * @return paginated list of location responses
     */
    Page<LocationResponse> getLocationsBySection(String section, Pageable pageable);
    
    /**
     * Get locations by event
     * 
     * @param eventId the event ID
     * @param pageable pagination information
     * @return paginated list of location responses
     */
    Page<LocationResponse> getLocationsByEvent(Long eventId, Pageable pageable);
    
    /**
     * Assign a project to a location
     * 
     * @param locationId the location ID
     * @param projectId the project ID
     * @param userId the ID of the user assigning the project
     * @return the updated location response
     */
    LocationResponse assignProjectToLocation(Long locationId, Long projectId, Long userId);
    
    /**
     * Unassign a project from a location
     * 
     * @param locationId the location ID
     * @param userId the ID of the user unassigning the project
     * @return the updated location response
     */
    LocationResponse unassignProjectFromLocation(Long locationId, Long userId);
    
    /**
     * Get location statistics
     * 
     * @return map of statistics
     */
    Map<String, Object> getLocationStatistics();
    
    /**
     * Delete a location
     * 
     * @param id the location ID
     */
    void deleteLocation(Long id);
    
    /**
     * Convert Location entity to LocationResponse DTO
     * 
     * @param location the location entity
     * @return the location response DTO
     */
    LocationResponse convertToDto(Location location);
    
    /**
     * Get all active locations by department
     * 
     * @param department the department
     * @return list of active locations
     */
    List<Location> findActiveLocationsByDepartment(Department department);
    
    /**
     * Get all active locations by event
     * 
     * @param event the event
     * @return list of active locations
     */
    List<Location> findActiveLocationsByEvent(Event event);
    
    /**
     * Import locations from a CSV file
     * 
     * @param file the CSV file
     * @param userId the ID of the user importing the locations
     * @return the import result
     */
    LocationImportResult importLocationsFromCsv(MultipartFile file, Long userId);
    
    /**
     * Export locations to a CSV file
     * 
     * @return the CSV file content as byte array
     */
    byte[] exportLocationsToCsv();
    
    /**
     * Create multiple locations in a batch
     * 
     * @param request the batch creation request
     * @param userId the ID of the user creating the locations
     * @return list of created location responses
     */
    List<LocationResponse> createLocationBatch(CreateLocationBatchRequest request, Long userId);
}
