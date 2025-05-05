package in.gppalanpur.portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Event;
import in.gppalanpur.portal.entity.Location;

/**
 * Repository for Location entity operations.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {
    
    /**
     * Find locations by department.
     * 
     * @param department Department to filter by
     * @return List of locations in the department
     */
    List<Location> findByDepartment(Department department);
    
    /**
     * Find locations by department with pagination.
     * 
     * @param department Department to filter by
     * @param pageable Pagination parameters
     * @return Page of locations in the department
     */
    Page<Location> findByDepartment(Department department, Pageable pageable);
    
    /**
     * Find active locations.
     * 
     * @return List of active locations
     */
    List<Location> findByIsActiveTrue();
    
    /**
     * Find active locations with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of active locations
     */
    Page<Location> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Find active locations by department.
     * 
     * @param department Department to filter by
     * @return List of active locations in the department
     */
    List<Location> findByDepartmentAndIsActiveTrue(Department department);
    
    /**
     * Find active locations by department with pagination.
     * 
     * @param department Department to filter by
     * @param pageable Pagination parameters
     * @return Page of active locations in the department
     */
    Page<Location> findByDepartmentAndIsActiveTrue(Department department, Pageable pageable);
    
    /**
     * Find locations by section.
     * 
     * @param section Section to filter by
     * @return List of locations in the section
     */
    List<Location> findBySection(String section);
    
    /**
     * Find locations by section with pagination.
     * 
     * @param section Section to filter by
     * @param pageable Pagination parameters
     * @return Page of locations in the section
     */
    Page<Location> findBySection(String section, Pageable pageable);
    
    /**
     * Find locations by building.
     * 
     * @param building Building to filter by
     * @return List of locations in the building
     */
    List<Location> findByBuilding(String building);
    
    /**
     * Find locations by building with pagination.
     * 
     * @param building Building to filter by
     * @param pageable Pagination parameters
     * @return Page of locations in the building
     */
    Page<Location> findByBuilding(String building, Pageable pageable);
    
    /**
     * Find locations by floor.
     * 
     * @param floor Floor to filter by
     * @return List of locations on the floor
     */
    List<Location> findByFloor(String floor);
    
    /**
     * Find locations by floor with pagination.
     * 
     * @param floor Floor to filter by
     * @param pageable Pagination parameters
     * @return Page of locations on the floor
     */
    Page<Location> findByFloor(String floor, Pageable pageable);
    
    /**
     * Count locations by department.
     * 
     * @param department Department to count locations for
     * @return Number of locations in the department
     */
    long countByDepartment(Department department);
    
    /**
     * Count active locations by department.
     * 
     * @param department Department to count active locations for
     * @return Number of active locations in the department
     */
    long countByDepartmentAndIsActiveTrue(Department department);
    
    /**
     * Count locations with active status.
     * 
     * @return Number of active locations
     */
    long countByIsActiveTrue();
    
    /**
     * Count locations with assigned projects.
     * 
     * @return Number of locations with assigned projects
     */
    long countByProjectIsNotNull();
    
    /**
     * Find locations by event.
     * 
     * @param event Event to filter by
     * @return List of locations for the event
     */
    List<Location> findByEvent(Event event);
    
    /**
     * Find locations by event with pagination.
     * 
     * @param event Event to filter by
     * @param pageable Pagination parameters
     * @return Page of locations for the event
     */
    Page<Location> findByEvent(Event event, Pageable pageable);
    
    /**
     * Find active locations by event.
     * 
     * @param event Event to filter by
     * @return List of active locations for the event
     */
    List<Location> findByEventAndIsActiveTrue(Event event);
    
    /**
     * Find active locations by event with pagination.
     * 
     * @param event Event to filter by
     * @param pageable Pagination parameters
     * @return Page of active locations for the event
     */
    Page<Location> findByEventAndIsActiveTrue(Event event, Pageable pageable);
}
