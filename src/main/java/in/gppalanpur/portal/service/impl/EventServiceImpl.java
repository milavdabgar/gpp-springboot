package in.gppalanpur.portal.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of EventService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

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
        Page<Event> eventsPage = eventRepository.findAll(pageable);
        List<EventResponse> eventResponses = eventsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(eventResponses, pageable, eventsPage.getTotalElements());
    }
    
    @Override
    public Page<EventResponse> getActiveEvents(Pageable pageable) {
        Page<Event> eventsPage = eventRepository.findByIsActiveTrue(pageable);
        List<EventResponse> eventResponses = eventsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(eventResponses, pageable, eventsPage.getTotalElements());
    }
    
    @Override
    public Page<EventResponse> getOngoingEvents(Pageable pageable) {
        LocalDate today = getCurrentDate();
        Page<Event> eventsPage = eventRepository.findOngoingEvents(today, pageable);
        List<EventResponse> eventResponses = eventsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(eventResponses, pageable, eventsPage.getTotalElements());
    }
    
    @Override
    public Page<EventResponse> getUpcomingEvents(Pageable pageable) {
        LocalDate today = getCurrentDate();
        Page<Event> eventsPage = eventRepository.findUpcomingEvents(today, pageable);
        List<EventResponse> eventResponses = eventsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(eventResponses, pageable, eventsPage.getTotalElements());
    }
    
    @Override
    public Page<EventResponse> getPastEvents(Pageable pageable) {
        LocalDate today = getCurrentDate();
        Page<Event> eventsPage = eventRepository.findPastEvents(today, pageable);
        List<EventResponse> eventResponses = eventsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(eventResponses, pageable, eventsPage.getTotalElements());
    }
    
    @Override
    public Page<EventResponse> getEventsWithOpenRegistration(Pageable pageable) {
        LocalDate today = getCurrentDate();
        Page<Event> eventsPage = eventRepository.findWithRegistrationOpen(today, pageable);
        List<EventResponse> eventResponses = eventsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(eventResponses, pageable, eventsPage.getTotalElements());
    }
    
    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        // Check if there are any projects associated with this event
        List<Project> projects = projectRepository.findByEvent(event);
        if (!projects.isEmpty()) {
            throw new IllegalStateException("Cannot delete event with associated projects");
        }
        
        eventRepository.delete(event);
    }
    
    @Override
    public Map<String, Object> getEventStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        LocalDate today = getCurrentDate();
        
        // Total events
        long totalEvents = eventRepository.count();
        statistics.put("totalEvents", totalEvents);
        
        // Active events
        long activeEvents = eventRepository.findByIsActiveTrue().size();
        statistics.put("activeEvents", activeEvents);
        
        // Current events
        long currentEvents = eventRepository.findOngoingEvents(today).size();
        statistics.put("currentEvents", currentEvents);
        
        // Upcoming events
        long upcomingEvents = eventRepository.findUpcomingEvents(today).size();
        statistics.put("upcomingEvents", upcomingEvents);
        
        // Past events
        long pastEvents = eventRepository.findPastEvents(today).size();
        statistics.put("pastEvents", pastEvents);
        
        // Open for registration
        long openForRegistration = eventRepository.findWithRegistrationOpen(today).size();
        statistics.put("openForRegistration", openForRegistration);
        
        return statistics;
    }
    
    @Override
    public List<Event> findActiveEvents() {
        return eventRepository.findByIsActiveTrue();
    }
    
    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now();
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
    public EventResponse convertToDto(Event event) {
        if (event == null) {
            return null;
        }
        
        LocalDate currentDate = getCurrentDate();
        boolean isRegistrationOpen = false;
        boolean isOngoing = false;
        
        if (event.getRegistrationStartDate() != null && event.getRegistrationEndDate() != null) {
            isRegistrationOpen = !currentDate.isBefore(event.getRegistrationStartDate()) && 
                                !currentDate.isAfter(event.getRegistrationEndDate());
        }
        
        if (event.getStartDate() != null && event.getEndDate() != null) {
            isOngoing = !currentDate.isBefore(event.getStartDate()) && 
                        !currentDate.isAfter(event.getEndDate());
        }
        
        boolean isPast = event.getEndDate() != null && currentDate.isAfter(event.getEndDate());
        
        // Get project count for this event
        int projectCount = 0;
        try {
            List<Project> projects = projectRepository.findByEvent(event);
            projectCount = projects.size();
        } catch (Exception e) {
            // Ignore exceptions when counting projects
            log.warn("Error counting projects for event {}: {}", event.getId(), e.getMessage());
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
}