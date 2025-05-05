package in.gppalanpur.portal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gppalanpur.portal.dto.ApiResponse;
import in.gppalanpur.portal.dto.PaginatedResponse;
import in.gppalanpur.portal.dto.event.CreateEventRequest;
import in.gppalanpur.portal.dto.event.EventResponse;
import in.gppalanpur.portal.dto.event.UpdateEventRequest;
import in.gppalanpur.portal.security.UserDetailsImpl;
import in.gppalanpur.portal.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management API")
public class EventController {

    private final EventService eventService;
    
    @GetMapping
    @Operation(summary = "Get all events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllEvents(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<EventResponse> eventsPage = eventService.getAllEvents(pageable);
        
        PaginatedResponse<EventResponse> paginatedResponse = PaginatedResponse.<EventResponse>builder()
                .page(eventsPage.getNumber() + 1)
                .limit(eventsPage.getSize())
                .total(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .build();
        
        ApiResponse<List<EventResponse>> response = ApiResponse.<List<EventResponse>>builder()
                .status("success")
                .message("Events retrieved successfully")
                .data(Map.of("events", eventsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    public ResponseEntity<ApiResponse<EventResponse>> getEvent(@PathVariable Long id) {
        EventResponse event = eventService.getEvent(id);
        
        ApiResponse<EventResponse> response = ApiResponse.<EventResponse>builder()
                .status("success")
                .message("Event retrieved successfully")
                .data(Map.of("event", event))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Create a new event")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        EventResponse event = eventService.createEvent(request, userDetails.getId());
        
        ApiResponse<EventResponse> response = ApiResponse.<EventResponse>builder()
                .status("success")
                .message("Event created successfully")
                .data(Map.of("event", event))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Update an event")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        EventResponse event = eventService.updateEvent(id, request, userDetails.getId());
        
        ApiResponse<EventResponse> response = ApiResponse.<EventResponse>builder()
                .status("success")
                .message("Event updated successfully")
                .data(Map.of("event", event))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Delete an event")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .message("Event deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getActiveEvents(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<EventResponse> eventsPage = eventService.getActiveEvents(pageable);
        
        PaginatedResponse<EventResponse> paginatedResponse = PaginatedResponse.<EventResponse>builder()
                .page(eventsPage.getNumber() + 1)
                .limit(eventsPage.getSize())
                .total(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .build();
        
        ApiResponse<List<EventResponse>> response = ApiResponse.<List<EventResponse>>builder()
                .status("success")
                .message("Active events retrieved successfully")
                .data(Map.of("events", eventsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/registration-open")
    @Operation(summary = "Get events with open registration")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsWithOpenRegistration(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<EventResponse> eventsPage = eventService.getEventsWithOpenRegistration(pageable);
        
        PaginatedResponse<EventResponse> paginatedResponse = PaginatedResponse.<EventResponse>builder()
                .page(eventsPage.getNumber() + 1)
                .limit(eventsPage.getSize())
                .total(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .build();
        
        ApiResponse<List<EventResponse>> response = ApiResponse.<List<EventResponse>>builder()
                .status("success")
                .message("Events with open registration retrieved successfully")
                .data(Map.of("events", eventsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/ongoing")
    @Operation(summary = "Get ongoing events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getOngoingEvents(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<EventResponse> eventsPage = eventService.getOngoingEvents(pageable);
        
        PaginatedResponse<EventResponse> paginatedResponse = PaginatedResponse.<EventResponse>builder()
                .page(eventsPage.getNumber() + 1)
                .limit(eventsPage.getSize())
                .total(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .build();
        
        ApiResponse<List<EventResponse>> response = ApiResponse.<List<EventResponse>>builder()
                .status("success")
                .message("Ongoing events retrieved successfully")
                .data(Map.of("events", eventsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getUpcomingEvents(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<EventResponse> eventsPage = eventService.getUpcomingEvents(pageable);
        
        PaginatedResponse<EventResponse> paginatedResponse = PaginatedResponse.<EventResponse>builder()
                .page(eventsPage.getNumber() + 1)
                .limit(eventsPage.getSize())
                .total(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .build();
        
        ApiResponse<List<EventResponse>> response = ApiResponse.<List<EventResponse>>builder()
                .status("success")
                .message("Upcoming events retrieved successfully")
                .data(Map.of("events", eventsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/past")
    @Operation(summary = "Get past events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getPastEvents(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<EventResponse> eventsPage = eventService.getPastEvents(pageable);
        
        PaginatedResponse<EventResponse> paginatedResponse = PaginatedResponse.<EventResponse>builder()
                .page(eventsPage.getNumber() + 1)
                .limit(eventsPage.getSize())
                .total(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .build();
        
        ApiResponse<List<EventResponse>> response = ApiResponse.<List<EventResponse>>builder()
                .status("success")
                .message("Past events retrieved successfully")
                .data(Map.of("events", eventsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/publish-results")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Publish results for an event")
    public ResponseEntity<ApiResponse<EventResponse>> publishResults(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        EventResponse event = eventService.publishResults(id, userDetails.getId());
        
        ApiResponse<EventResponse> response = ApiResponse.<EventResponse>builder()
                .status("success")
                .message("Event results published successfully")
                .data(Map.of("event", event))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
    @Operation(summary = "Get event statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEventStatistics() {
        Map<String, Object> statistics = eventService.getEventStatistics();
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Event statistics retrieved successfully")
                .data(Map.of("statistics", statistics))
                .build();
        
        return ResponseEntity.ok(response);
    }
}
