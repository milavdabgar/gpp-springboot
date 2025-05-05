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
import in.gppalanpur.portal.dto.team.CreateTeamRequest;
import in.gppalanpur.portal.dto.team.TeamMemberRequest;
import in.gppalanpur.portal.dto.team.TeamResponse;
import in.gppalanpur.portal.dto.team.UpdateTeamRequest;
import in.gppalanpur.portal.security.UserDetailsImpl;
import in.gppalanpur.portal.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "Team management API")
public class TeamController {

    private final TeamService teamService;
    
    @GetMapping
    @Operation(summary = "Get all teams")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getAllTeams(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<TeamResponse> teamsPage = teamService.getAllTeams(pageable);
        
        PaginatedResponse<TeamResponse> paginatedResponse = PaginatedResponse.<TeamResponse>builder()
                .page(teamsPage.getNumber() + 1)
                .limit(teamsPage.getSize())
                .total(teamsPage.getTotalElements())
                .totalPages(teamsPage.getTotalPages())
                .build();
        
        ApiResponse<List<TeamResponse>> response = ApiResponse.<List<TeamResponse>>builder()
                .status("success")
                .message("Teams retrieved successfully")
                .data(Map.of("teams", teamsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeam(@PathVariable Long id) {
        TeamResponse team = teamService.getTeam(id);
        
        ApiResponse<TeamResponse> response = ApiResponse.<TeamResponse>builder()
                .status("success")
                .message("Team retrieved successfully")
                .data(Map.of("team", team))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(summary = "Create a new team")
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @Valid @RequestBody CreateTeamRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        TeamResponse team = teamService.createTeam(request, userDetails.getId());
        
        ApiResponse<TeamResponse> response = ApiResponse.<TeamResponse>builder()
                .status("success")
                .message("Team created successfully")
                .data(Map.of("team", team))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a team")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTeamRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        TeamResponse team = teamService.updateTeam(id, request, userDetails.getId());
        
        ApiResponse<TeamResponse> response = ApiResponse.<TeamResponse>builder()
                .status("success")
                .message("Team updated successfully")
                .data(Map.of("team", team))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_admin')")
    @Operation(summary = "Delete a team")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .message("Team deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get teams by department")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getTeamsByDepartment(
            @PathVariable Long departmentId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<TeamResponse> teamsPage = teamService.getTeamsByDepartment(departmentId, pageable);
        
        PaginatedResponse<TeamResponse> paginatedResponse = PaginatedResponse.<TeamResponse>builder()
                .page(teamsPage.getNumber() + 1)
                .limit(teamsPage.getSize())
                .total(teamsPage.getTotalElements())
                .totalPages(teamsPage.getTotalPages())
                .build();
        
        ApiResponse<List<TeamResponse>> response = ApiResponse.<List<TeamResponse>>builder()
                .status("success")
                .message("Department teams retrieved successfully")
                .data(Map.of("teams", teamsPage.getContent()))
                .pagination(paginatedResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-teams")
    @Operation(summary = "Get teams for the current user")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getMyTeams(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<TeamResponse> teams = teamService.getTeamsByMember(userDetails.getId());
        
        ApiResponse<List<TeamResponse>> response = ApiResponse.<List<TeamResponse>>builder()
                .status("success")
                .message("My teams retrieved successfully")
                .data(Map.of("teams", teams))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/leader")
    @Operation(summary = "Get teams led by the current user")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getTeamsLedByMe(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<TeamResponse> teams = teamService.getTeamsByLeader(userDetails.getId());
        
        ApiResponse<List<TeamResponse>> response = ApiResponse.<List<TeamResponse>>builder()
                .status("success")
                .message("Teams led by me retrieved successfully")
                .data(Map.of("teams", teams))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/members")
    @Operation(summary = "Add a member to a team")
    public ResponseEntity<ApiResponse<TeamResponse>> addTeamMember(
            @PathVariable Long id,
            @Valid @RequestBody TeamMemberRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        TeamResponse team = teamService.addTeamMember(id, request.getUserId(), userDetails.getId());
        
        ApiResponse<TeamResponse> response = ApiResponse.<TeamResponse>builder()
                .status("success")
                .message("Member added to team successfully")
                .data(Map.of("team", team))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}/members/{memberId}")
    @Operation(summary = "Remove a member from a team")
    public ResponseEntity<ApiResponse<TeamResponse>> removeTeamMember(
            @PathVariable Long id,
            @PathVariable Long memberId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        TeamResponse team = teamService.removeTeamMember(id, memberId, userDetails.getId());
        
        ApiResponse<TeamResponse> response = ApiResponse.<TeamResponse>builder()
                .status("success")
                .message("Member removed from team successfully")
                .data(Map.of("team", team))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/leader/{leaderId}")
    @Operation(summary = "Set team leader")
    public ResponseEntity<ApiResponse<TeamResponse>> setTeamLeader(
            @PathVariable Long id,
            @PathVariable Long leaderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        TeamResponse team = teamService.setTeamLeader(id, leaderId, userDetails.getId());
        
        ApiResponse<TeamResponse> response = ApiResponse.<TeamResponse>builder()
                .status("success")
                .message("Team leader set successfully")
                .data(Map.of("team", team))
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_principal')")
    @Operation(summary = "Get team statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTeamStatistics() {
        Map<String, Object> statistics = teamService.getTeamStatistics();
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Team statistics retrieved successfully")
                .data(Map.of("statistics", statistics))
                .build();
        
        return ResponseEntity.ok(response);
    }
}
