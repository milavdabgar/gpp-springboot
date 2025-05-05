package in.gppalanpur.portal.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import in.gppalanpur.portal.dto.event.CreateEventRequest;
import in.gppalanpur.portal.dto.event.EventResponse;
import in.gppalanpur.portal.dto.event.UpdateEventRequest;
import in.gppalanpur.portal.entity.Event;

/**
 * Service interface for Event operations.
 */
public interface EventService {
    
    /**
     * Create a new event
     * 
     * @param request the event creation request
     * @param userId the ID of the user creating the event
     * @return the created event response
     */
    EventResponse createEvent(CreateEventRequest request, Long userId);
    
    /**
     * Update an existing event
     * 
     * @param id the event ID
     * @param request the event update request
     * @param userId the ID of the user updating the event
     * @return the updated event response
     */
    EventResponse updateEvent(Long id, UpdateEventRequest request, Long userId);
    
    /**
     * Get an event by ID
     * 
     * @param id the event ID
     * @return the event response
     */
    EventResponse getEvent(Long id);
    
    /**
     * Get all events with pagination
     * 
     * @param pageable pagination information
     * @return paginated list of event responses
     */
    Page<EventResponse> getAllEvents(Pageable pageable);
    
    /**
     * Get active events with pagination
     * 
     * @param pageable pagination information
     * @return paginated list of active event responses
     */
    Page<EventResponse> getActiveEvents(Pageable pageable);
    
    /**
     * Get events with open registration
     * 
     * @param pageable pagination information
     * @return paginated list of events with open registration
     */
    Page<EventResponse> getEventsWithOpenRegistration(Pageable pageable);
    
    /**
     * Get ongoing events
     * 
     * @param pageable pagination information
     * @return paginated list of ongoing event responses
     */
    Page<EventResponse> getOngoingEvents(Pageable pageable);
    
    /**
     * Get upcoming events
     * 
     * @param pageable pagination information
     * @return paginated list of upcoming event responses
     */
    Page<EventResponse> getUpcomingEvents(Pageable pageable);
    
    /**
     * Get past events
     * 
     * @param pageable pagination information
     * @return paginated list of past event responses
     */
    Page<EventResponse> getPastEvents(Pageable pageable);
    
    /**
     * Publish results for an event
     * 
     * @param id the event ID
     * @param userId the ID of the user publishing the results
     * @return the updated event response
     */
    EventResponse publishResults(Long id, Long userId);
    
    /**
     * Get event statistics
     * 
     * @return map of statistics
     */
    Map<String, Object> getEventStatistics();
    
    /**
     * Delete an event
     * 
     * @param id the event ID
     */
    void deleteEvent(Long id);
    
    /**
     * Convert Event entity to EventResponse DTO
     * 
     * @param event the event entity
     * @return the event response DTO
     */
    EventResponse convertToDto(Event event);
    
    /**
     * Get all active events
     * 
     * @return list of active events
     */
    List<Event> findActiveEvents();
    
    /**
     * Get current date
     * 
     * @return current date
     */
    LocalDate getCurrentDate();
}
