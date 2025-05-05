package in.gppalanpur.portal.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import in.gppalanpur.portal.dto.team.CreateTeamRequest;
import in.gppalanpur.portal.dto.team.TeamResponse;
import in.gppalanpur.portal.dto.team.UpdateTeamRequest;
import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Team;

/**
 * Service interface for Team operations.
 */
public interface TeamService {
    
    /**
     * Create a new team
     * 
     * @param request the team creation request
     * @param userId the ID of the user creating the team
     * @return the created team response
     */
    TeamResponse createTeam(CreateTeamRequest request, Long userId);
    
    /**
     * Update an existing team
     * 
     * @param id the team ID
     * @param request the team update request
     * @param userId the ID of the user updating the team
     * @return the updated team response
     */
    TeamResponse updateTeam(Long id, UpdateTeamRequest request, Long userId);
    
    /**
     * Get a team by ID
     * 
     * @param id the team ID
     * @return the team response
     */
    TeamResponse getTeam(Long id);
    
    /**
     * Get all teams with pagination
     * 
     * @param pageable pagination information
     * @return paginated list of team responses
     */
    Page<TeamResponse> getAllTeams(Pageable pageable);
    
    /**
     * Get teams by department
     * 
     * @param departmentId the department ID
     * @param pageable pagination information
     * @return paginated list of team responses
     */
    Page<TeamResponse> getTeamsByDepartment(Long departmentId, Pageable pageable);
    
    /**
     * Get teams by member
     * 
     * @param userId the user ID
     * @return list of team responses
     */
    List<TeamResponse> getTeamsByMember(Long userId);
    
    /**
     * Get teams by leader
     * 
     * @param userId the user ID
     * @return list of team responses
     */
    List<TeamResponse> getTeamsByLeader(Long userId);
    
    /**
     * Add a member to a team
     * 
     * @param teamId the team ID
     * @param memberId the member ID
     * @param userId the ID of the user adding the member
     * @return the updated team response
     */
    TeamResponse addTeamMember(Long teamId, Long memberId, Long userId);
    
    /**
     * Remove a member from a team
     * 
     * @param teamId the team ID
     * @param memberId the member ID
     * @param userId the ID of the user removing the member
     * @return the updated team response
     */
    TeamResponse removeTeamMember(Long teamId, Long memberId, Long userId);
    
    /**
     * Set team leader
     * 
     * @param teamId the team ID
     * @param leaderId the leader ID
     * @param userId the ID of the user setting the leader
     * @return the updated team response
     */
    TeamResponse setTeamLeader(Long teamId, Long leaderId, Long userId);
    
    /**
     * Get team statistics
     * 
     * @return map of statistics
     */
    Map<String, Object> getTeamStatistics();
    
    /**
     * Delete a team
     * 
     * @param id the team ID
     */
    void deleteTeam(Long id);
    
    /**
     * Convert Team entity to TeamResponse DTO
     * 
     * @param team the team entity
     * @return the team response DTO
     */
    TeamResponse convertToDto(Team team);
    
    /**
     * Get all active teams by department
     * 
     * @param department the department
     * @return list of team entities
     */
    List<Team> findActiveTeamsByDepartment(Department department);
}
