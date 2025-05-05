package in.gppalanpur.portal.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.department.CreateDepartmentRequest;
import in.gppalanpur.portal.dto.department.DepartmentImportResult;
import in.gppalanpur.portal.dto.department.DepartmentResponse;
import in.gppalanpur.portal.dto.department.UpdateDepartmentRequest;

public interface DepartmentService {

    List<DepartmentResponse> getAllDepartments();
    
    DepartmentResponse getDepartment(Long id);
    
    DepartmentResponse createDepartment(CreateDepartmentRequest request);
    
    DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request);
    
    void deleteDepartment(Long id);
    
    Map<String, Object> getDepartmentStats();
    
    DepartmentImportResult importDepartments(MultipartFile file);
    
    byte[] exportDepartments();
}