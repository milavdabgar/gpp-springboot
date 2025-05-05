package in.gppalanpur.portal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gppalanpur.portal.dto.project.TeamMemberDto;
import in.gppalanpur.portal.dto.team.CreateTeamRequest;
import in.gppalanpur.portal.dto.team.TeamResponse;
import in.gppalanpur.portal.dto.team.UpdateTeamRequest;
import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Team;
import in.gppalanpur.portal.entity.User;
import in.gppalanpur.portal.exception.ResourceNotFoundException;
import in.gppalanpur.portal.repository.DepartmentRepository;
import in.gppalanpur.portal.repository.TeamRepository;
import in.gppalanpur.portal.repository.UserRepository;
import in.gppalanpur.portal.service.TeamService;

/**
 * Implementation of TeamService interface.
 */
@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public TeamResponse createTeam(CreateTeamRequest request, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
        
        User leader = null;
        if (request.getLeaderId() != null) {
            leader = userRepository.findById(request.getLeaderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leader not found with id: " + request.getLeaderId()));
        }
        
        Team team = Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .department(department)
                .leader(leader)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .createdBy(creator)
                .updatedBy(creator)
                .build();
        
        team = teamRepository.save(team);
        
        // Add members if provided
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            List<User> members = userRepository.findAllById(request.getMemberIds());
            team.setMembers(members);
            team = teamRepository.save(team);
        }
        
        return convertToDto(team);
    }

    @Override
    @Transactional
    public TeamResponse updateTeam(Long id, UpdateTeamRequest request, Long userId) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (request.getName() != null) {
            team.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            team.setDescription(request.getDescription());
        }
        
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
            team.setDepartment(department);
        }
        
        if (request.getLeaderId() != null) {
            User leader = userRepository.findById(request.getLeaderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leader not found with id: " + request.getLeaderId()));
            team.setLeader(leader);
        }
        
        if (request.getIsActive() != null) {
            team.setActive(request.getIsActive());
        }
        
        if (request.getMemberIds() != null) {
            List<User> members = userRepository.findAllById(request.getMemberIds());
            team.setMembers(members);
        }
        
        team.setUpdatedBy(updater);
        team = teamRepository.save(team);
        
        return convertToDto(team);
    }

    @Override
    public TeamResponse getTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        return convertToDto(team);
    }

    @Override
    public Page<TeamResponse> getAllTeams(Pageable pageable) {
        return teamRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<TeamResponse> getTeamsByDepartment(Long departmentId, Pageable pageable) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
        
        return teamRepository.findByDepartment(department, pageable)
                .map(this::convertToDto);
    }
    
    @Override
    public List<TeamResponse> getTeamsByMember(Long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Team> teams = teamRepository.findByMemberId(userId);
        return teams.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TeamResponse> getTeamsByLeader(Long userId) {
        User leader = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Team> teams = teamRepository.findByLeader(leader);
        return teams.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamResponse addTeamMember(Long teamId, Long memberId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + memberId));
        
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<User> members = team.getMembers();
        if (members == null) {
            members = new ArrayList<>();
        }
        
        if (!members.contains(member)) {
            members.add(member);
            team.setMembers(members);
            team.setUpdatedBy(updater);
            team = teamRepository.save(team);
        }
        
        return convertToDto(team);
    }

    @Override
    @Transactional
    public TeamResponse removeTeamMember(Long teamId, Long memberId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + memberId));
        
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<User> members = team.getMembers();
        if (members != null && members.contains(member)) {
            members.remove(member);
            team.setMembers(members);
            team.setUpdatedBy(updater);
            team = teamRepository.save(team);
        }
        
        return convertToDto(team);
    }
    
    @Override
    @Transactional
    public TeamResponse setTeamLeader(Long teamId, Long leaderId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        
        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + leaderId));
        
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        team.setLeader(leader);
        team.setUpdatedBy(updater);
        team = teamRepository.save(team);
        
        return convertToDto(team);
    }
    
    @Override
    public Map<String, Object> getTeamStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Total teams
        long totalTeams = teamRepository.count();
        statistics.put("totalTeams", totalTeams);
        
        // Active teams
        long activeTeams = teamRepository.findByIsActiveTrue().size();
        statistics.put("activeTeams", activeTeams);
        
        // Teams by department
        List<Department> departments = departmentRepository.findAll();
        Map<String, Long> teamsByDepartment = new HashMap<>();
        
        for (Department department : departments) {
            long count = teamRepository.countByDepartment(department);
            teamsByDepartment.put(department.getName(), count);
        }
        
        statistics.put("teamsByDepartment", teamsByDepartment);
        
        return statistics;
    }

    @Override
    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        
        teamRepository.delete(team);
    }

    @Override
    public TeamResponse convertToDto(Team team) {
        if (team == null) {
            return null;
        }
        
        List<TeamMemberDto> memberDtos = new ArrayList<>();
        if (team.getMembers() != null) {
            memberDtos = team.getMembers().stream()
                .map(member -> TeamMemberDto.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .departmentName(member.getDepartment() != null ? member.getDepartment().getName() : null)
                    .isLeader(team.getLeader() != null && team.getLeader().getId().equals(member.getId()))
                    .build())
                .collect(Collectors.toList());
        }
        
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .departmentId(team.getDepartment() != null ? team.getDepartment().getId() : null)
                .departmentName(team.getDepartment() != null ? team.getDepartment().getName() : null)
                .leaderId(team.getLeader() != null ? team.getLeader().getId() : null)
                .leaderName(team.getLeader() != null ? team.getLeader().getName() : null)
                .leaderEmail(team.getLeader() != null ? team.getLeader().getEmail() : null)
                .members(memberDtos)
                .isActive(team.isActive())
                .createdById(team.getCreatedBy() != null ? team.getCreatedBy().getId() : null)
                .createdByName(team.getCreatedBy() != null ? team.getCreatedBy().getName() : null)
                .updatedById(team.getUpdatedBy() != null ? team.getUpdatedBy().getId() : null)
                .updatedByName(team.getUpdatedBy() != null ? team.getUpdatedBy().getName() : null)
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }

    @Override
    public List<Team> findActiveTeamsByDepartment(Department department) {
        return teamRepository.findByDepartmentAndIsActiveTrue(department);
    }
}