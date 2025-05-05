package in.gppalanpur.portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.Team;
import in.gppalanpur.portal.entity.User;

/**
 * Repository for Team entity operations.
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    /**
     * Find teams by department.
     * 
     * @param department Department to filter by
     * @return List of teams in the department
     */
    List<Team> findByDepartment(Department department);
    
    /**
     * Find teams by department with pagination.
     * 
     * @param department Department to filter by
     * @param pageable Pagination parameters
     * @return Page of teams in the department
     */
    Page<Team> findByDepartment(Department department, Pageable pageable);
    
    /**
     * Find teams by leader.
     * 
     * @param leader Team leader to filter by
     * @return List of teams led by the user
     */
    List<Team> findByLeader(User leader);
    
    /**
     * Find teams by member.
     * 
     * @param memberId ID of the team member to filter by
     * @return List of teams that include the member
     */
    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.id = :memberId")
    List<Team> findByMemberId(@Param("memberId") Long memberId);
    
    /**
     * Find active teams.
     * 
     * @return List of active teams
     */
    List<Team> findByIsActiveTrue();
    
    /**
     * Find active teams with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of active teams
     */
    Page<Team> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Find active teams by department.
     * 
     * @param department Department to filter by
     * @return List of active teams in the department
     */
    List<Team> findByDepartmentAndIsActiveTrue(Department department);
    
    /**
     * Find active teams by department with pagination.
     * 
     * @param department Department to filter by
     * @param pageable Pagination parameters
     * @return Page of active teams in the department
     */
    Page<Team> findByDepartmentAndIsActiveTrue(Department department, Pageable pageable);
    
    /**
     * Count teams by department.
     * 
     * @param department Department to count teams for
     * @return Number of teams in the department
     */
    long countByDepartment(Department department);
    
    /**
     * Count active teams by department.
     * 
     * @param department Department to count active teams for
     * @return Number of active teams in the department
     */
    long countByDepartmentAndIsActiveTrue(Department department);
}
