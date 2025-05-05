package in.gppalanpur.portal.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gppalanpur.portal.dto.event.CreateEventRequest;
import in.gppalanpur.portal.dto.event.EventResponse;
import in.gppalanpur.portal.dto.event.UpdateEventRequest;
import in.gppalanpur.portal.entity.Event;
import in.gppalanpur.portal.entity.Project;
import in.gppalanpur.portal.entity.User;
import in.gppalanpur.portal.exception.ResourceNotFoundException;
import in.gppalanpur.portal.repository.EventRepository;
import in.gppalanpur.portal.repository.ProjectRepository;
import in.gppalanpur.portal.repository.UserRepository;
import in.gppalanpur.portal.service.EventService;

/**
 * Implementation of EventService interface.
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest request, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Event event = Event.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .registrationStartDate(request.getRegistrationStartDate())
                .registrationEndDate(request.getRegistrationEndDate())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .resultsPublished(false)
                .createdBy(creator)
                .updatedBy(creator)
                .build();
        
        event = eventRepository.save(event);
        
        return convertToDto(event);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long id, UpdateEventRequest request, Long userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (request.getName() != null) {
            event.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        
        if (request.getStartDate() != null) {
            event.setStartDate(request.getStartDate());
        }
        
        if (request.getEndDate() != null) {
            event.setEndDate(request.getEndDate());
        }
        
        if (request.getRegistrationStartDate() != null) {
            event.setRegistrationStartDate(request.getRegistrationStartDate());
        }
        
        if (request.getRegistrationEndDate() != null) {
            event.setRegistrationEndDate(request.getRegistrationEndDate());
        }
        
        if (request.getResultsPublished() != null) {
            event.setResultsPublished(request.getResultsPublished());
        }
        
        if (request.getIsActive() != null) {
            event.setActive(request.getIsActive());
        }
        
        event.setUpdatedBy(updater);
        event = eventRepository.save(event);
        
        return convertToDto(event);
    }

    @Override
    public EventResponse getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return convertToDto(event);
    }

    @Override
    public Page<EventResponse> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<EventResponse> getActiveEvents(Pageable pageable) {
        return eventRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<EventResponse> getEventsWithOpenRegistration(Pageable pageable) {
        LocalDate currentDate = getCurrentDate();
        return eventRepository.findWithRegistrationOpen(currentDate, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<EventResponse> getOngoingEvents(Pageable pageable) {
        LocalDate currentDate = getCurrentDate();
        return eventRepository.findOngoingEvents(currentDate, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<EventResponse> getUpcomingEvents(Pageable pageable) {
        LocalDate currentDate = getCurrentDate();
        return eventRepository.findUpcomingEvents(currentDate, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<EventResponse> getPastEvents(Pageable pageable) {
        LocalDate currentDate = getCurrentDate();
        return eventRepository.findPastEvents(currentDate, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public EventResponse publishResults(Long id, Long userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        event.setResultsPublished(true);
        event.setUpdatedBy(updater);
        event = eventRepository.save(event);
        
        return convertToDto(event);
    }

    @Override
    public Map<String, Object> getEventStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Total events
        long totalEvents = eventRepository.count();
        statistics.put("totalEvents", totalEvents);
        
        // Active events
        long activeEvents = eventRepository.findByIsActiveTrue().size();
        statistics.put("activeEvents", activeEvents);
        
        // Events with published results
        long eventsWithPublishedResults = eventRepository.findByResultsPublishedTrue().size();
        statistics.put("eventsWithPublishedResults", eventsWithPublishedResults);
        
        // Current date for calculations
        LocalDate currentDate = getCurrentDate();
        
        // Ongoing events
        long ongoingEvents = eventRepository.findOngoingEvents(currentDate).size();
        statistics.put("ongoingEvents", ongoingEvents);
        
        // Upcoming events
        long upcomingEvents = eventRepository.findUpcomingEvents(currentDate).size();
        statistics.put("upcomingEvents", upcomingEvents);
        
        // Past events
        long pastEvents = eventRepository.findPastEvents(currentDate).size();
        statistics.put("pastEvents", pastEvents);
        
        // Events with open registration
        long eventsWithOpenRegistration = eventRepository.findWithRegistrationOpen(currentDate).size();
        statistics.put("eventsWithOpenRegistration", eventsWithOpenRegistration);
        
        return statistics;
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        eventRepository.delete(event);
    }

    @Override
    public EventResponse convertToDto(Event event) {
        if (event == null) {
            return null;
        }
        
        // Get current date for calculations
        LocalDate currentDate = getCurrentDate();
        
        // Calculate additional fields for frontend
        boolean isRegistrationOpen = event.isActive() && 
                event.getRegistrationStartDate() != null && 
                event.getRegistrationEndDate() != null && 
                !currentDate.isBefore(event.getRegistrationStartDate()) && 
                !currentDate.isAfter(event.getRegistrationEndDate());
        
        boolean isOngoing = event.isActive() && 
                !currentDate.isBefore(event.getStartDate()) && 
                !currentDate.isAfter(event.getEndDate());
        
        boolean isPast = currentDate.isAfter(event.getEndDate());
        
        // Get project count for this event
        int projectCount = 0;
        try {
            List<Project> projects = projectRepository.findByEvent(event);
            projectCount = projects.size();
        } catch (Exception e) {
            // Ignore exceptions when counting projects
        }
        
        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .registrationStartDate(event.getRegistrationStartDate())
                .registrationEndDate(event.getRegistrationEndDate())
                .resultsPublished(event.isResultsPublished())
                .isActive(event.isActive())
                .createdById(event.getCreatedBy() != null ? event.getCreatedBy().getId() : null)
                .createdByName(event.getCreatedBy() != null ? event.getCreatedBy().getName() : null)
                .updatedById(event.getUpdatedBy() != null ? event.getUpdatedBy().getId() : null)
                .updatedByName(event.getUpdatedBy() != null ? event.getUpdatedBy().getName() : null)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .isRegistrationOpen(isRegistrationOpen)
                .isOngoing(isOngoing)
                .isPast(isPast)
                .projectCount(projectCount)
                .build();
    }

    @Override
    public List<Event> findActiveEvents() {
        return eventRepository.findByIsActiveTrue();
    }

    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}
