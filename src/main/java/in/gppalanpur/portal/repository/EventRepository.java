package in.gppalanpur.portal.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Event;

/**
 * Repository for Event entity operations.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * Find active events.
     * 
     * @return List of active events
     */
    List<Event> findByIsActiveTrue();
    
    /**
     * Find active events with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of active events
     */
    Page<Event> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Find events with registration open.
     * 
     * @param currentDate Current date to check against registration dates
     * @return List of events with open registration
     */
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.registrationStartDate <= :currentDate AND e.registrationEndDate >= :currentDate")
    List<Event> findWithRegistrationOpen(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find events with registration open with pagination.
     * 
     * @param currentDate Current date to check against registration dates
     * @param pageable Pagination parameters
     * @return Page of events with open registration
     */
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.registrationStartDate <= :currentDate AND e.registrationEndDate >= :currentDate")
    Page<Event> findWithRegistrationOpen(@Param("currentDate") LocalDate currentDate, Pageable pageable);
    
    /**
     * Find ongoing events.
     * 
     * @param currentDate Current date to check against event dates
     * @return List of ongoing events
     */
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.startDate <= :currentDate AND e.endDate >= :currentDate")
    List<Event> findOngoingEvents(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find ongoing events with pagination.
     * 
     * @param currentDate Current date to check against event dates
     * @param pageable Pagination parameters
     * @return Page of ongoing events
     */
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.startDate <= :currentDate AND e.endDate >= :currentDate")
    Page<Event> findOngoingEvents(@Param("currentDate") LocalDate currentDate, Pageable pageable);
    
    /**
     * Find upcoming events.
     * 
     * @param currentDate Current date to check against event dates
     * @return List of upcoming events
     */
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.startDate > :currentDate")
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find upcoming events with pagination.
     * 
     * @param currentDate Current date to check against event dates
     * @param pageable Pagination parameters
     * @return Page of upcoming events
     */
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.startDate > :currentDate")
    Page<Event> findUpcomingEvents(@Param("currentDate") LocalDate currentDate, Pageable pageable);
    
    /**
     * Find past events.
     * 
     * @param currentDate Current date to check against event dates
     * @return List of past events
     */
    @Query("SELECT e FROM Event e WHERE e.endDate < :currentDate")
    List<Event> findPastEvents(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find past events with pagination.
     * 
     * @param currentDate Current date to check against event dates
     * @param pageable Pagination parameters
     * @return Page of past events
     */
    @Query("SELECT e FROM Event e WHERE e.endDate < :currentDate")
    Page<Event> findPastEvents(@Param("currentDate") LocalDate currentDate, Pageable pageable);
    
    /**
     * Find events with published results.
     * 
     * @return List of events with published results
     */
    List<Event> findByResultsPublishedTrue();
    
    /**
     * Find events with published results with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of events with published results
     */
    Page<Event> findByResultsPublishedTrue(Pageable pageable);
}
