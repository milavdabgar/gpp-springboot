package in.gppalanpur.portal.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.department.CreateDepartmentRequest;
import in.gppalanpur.portal.dto.department.DepartmentImportResult;
import in.gppalanpur.portal.dto.department.DepartmentResponse;
import in.gppalanpur.portal.dto.department.UpdateDepartmentRequest;
import in.gppalanpur.portal.entity.Department;
import in.gppalanpur.portal.entity.User;
import in.gppalanpur.portal.exception.BadRequestException;
import in.gppalanpur.portal.exception.ResourceNotFoundException;
import in.gppalanpur.portal.repository.DepartmentRepository;
import in.gppalanpur.portal.repository.UserRepository;
import in.gppalanpur.portal.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    
    private static final String[] CSV_HEADERS = {
            "Name", "Code", "Description", "EstablishedDate", "IsActive"
    };

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(this::mapToDepartmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponse getDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        
        return mapToDepartmentResponse(department);
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        // Validate name uniqueness
        if (departmentRepository.existsByName(request.getName())) {
            throw new BadRequestException("Department name is already in use");
        }
        
        // Validate code uniqueness
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Department code is already in use");
        }
        
        // Validate HOD if provided
        User hod = null;
        if (request.getHodId() != null) {
            hod = userRepository.findById(request.getHodId())
                    .orElseThrow(() -> new ResourceNotFoundException("HOD user not found"));
            
            // Ensure user has HOD role
            if (!hod.getRoles().contains("hod")) {
                throw new BadRequestException("User must have HOD role to be assigned as department head");
            }
        }
        
        // Create department
        Department department = Department.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .hodId(hod)
                .establishedDate(request.getEstablishedDate())
                .isActive(request.isActive())
                .build();
        
        Department savedDepartment = departmentRepository.save(department);
        
        return mapToDepartmentResponse(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        
        // Update name if provided
        if (request.getName() != null && !request.getName().isEmpty() 
                && !request.getName().equals(department.getName())) {
            
            // Check name uniqueness
            if (departmentRepository.existsByName(request.getName())) {
                throw new BadRequestException("Department name is already in use");
            }
            
            department.setName(request.getName());
        }
        
        // Update code if provided
        if (request.getCode() != null && !request.getCode().isEmpty() 
                && !request.getCode().equals(department.getCode())) {
            
            // Check code uniqueness
            if (departmentRepository.existsByCode(request.getCode())) {
                throw new BadRequestException("Department code is already in use");
            }
            
            department.setCode(request.getCode());
        }
        
        // Update description if provided
        if (request.getDescription() != null) {
            department.setDescription(request.getDescription());
        }
        
        // Update HOD if provided
        if (request.getHodId() != null) {
            if (request.getHodId().toString().trim().isEmpty()) {
                // If empty string is provided, set HOD to null
                department.setHodId(null);
            } else {
                User hod = userRepository.findById(request.getHodId())
                        .orElseThrow(() -> new ResourceNotFoundException("HOD user not found"));
                
                // Ensure user has HOD role
                if (!hod.getRoles().contains("hod")) {
                    throw new BadRequestException("User must have HOD role to be assigned as department head");
                }
                
                department.setHodId(hod);
            }
        }
        
        // Update established date if provided
        if (request.getEstablishedDate() != null) {
            department.setEstablishedDate(request.getEstablishedDate());
        }
        
        // Update active status if provided
        if (request.getIsActive() != null) {
            department.setActive(request.getIsActive());
        }
        
        Department updatedDepartment = departmentRepository.save(department);
        
        return mapToDepartmentResponse(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        
        departmentRepository.delete(department);
    }

    @Override
    public Map<String, Object> getDepartmentStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get total count
        long totalCount = departmentRepository.count();
        stats.put("totalCount", totalCount);
        
        // Get active count
        long activeCount = departmentRepository.findByIsActive(true).size();
        stats.put("activeCount", activeCount);
        
        // Get inactive count
        long inactiveCount = departmentRepository.findByIsActive(false).size();
        stats.put("inactiveCount", inactiveCount);
        
        return stats;
    }

    @Override
    @Transactional
    public DepartmentImportResult importDepartments(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Please upload a CSV file");
        }
        
        if (!file.getOriginalFilename().endsWith(".csv")) {
            throw new BadRequestException("Please upload a valid CSV file");
        }
        
        List<DepartmentResponse> successfulImports = new ArrayList<>();
        List<DepartmentImportResult.FailedImport> failedImports = new ArrayList<>();
        
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {
            
            for (CSVRecord record : csvParser) {
                try {
                    // Extract fields from CSV
                    String name = record.get("Name");
                    String code = record.get("Code") != null ? record.get("Code").toUpperCase() : null;
                    String description = record.get("Description");
                    String establishedDateStr = record.get("EstablishedDate");
                    String isActiveStr = record.get("IsActive");
                    
                    // Validate required fields
                    if (name == null || name.isEmpty() || code == null || code.isEmpty()
                            || description == null || description.isEmpty() || establishedDateStr == null || establishedDateStr.isEmpty()) {
                        failedImports.add(createFailedImport(record, "Missing required fields"));
                        continue;
                    }
                    
                    // Parse established date
                    LocalDate establishedDate;
                    try {
                        establishedDate = LocalDate.parse(establishedDateStr);
                    } catch (Exception e) {
                        failedImports.add(createFailedImport(record, "Invalid established date format"));
                        continue;
                    }
                    
                    // Parse active status
                    boolean isActive = isActiveStr == null || isActiveStr.isEmpty() 
                            || isActiveStr.equalsIgnoreCase("true") || isActiveStr.equals("1");
                    
                    // Try to update existing department, if not found create new one
                    try {
                        Department department = departmentRepository.findByCode(code)
                                .orElse(Department.builder()
                                        .name(name)
                                        .code(code)
                                        .description(description)
                                        .establishedDate(establishedDate)
                                        .isActive(isActive)
                                        .build());
                        
                        // Update fields if department already exists
                        if (department.getId() != null) {
                            department.setName(name);
                            department.setDescription(description);
                            department.setEstablishedDate(establishedDate);
                            department.setActive(isActive);
                        }
                        
                        Department savedDepartment = departmentRepository.save(department);
                        successfulImports.add(mapToDepartmentResponse(savedDepartment));
                    } catch (Exception e) {
                        failedImports.add(createFailedImport(record, e.getMessage()));
                    }
                    
                } catch (Exception e) {
                    failedImports.add(createFailedImport(record, "Error processing department: " + e.getMessage()));
                }
            }
            
        } catch (IOException e) {
            throw new BadRequestException("Failed to parse CSV file: " + e.getMessage());
        }
        
        // Build message
        String message = String.format("%d departments processed (%d failed)", 
                successfulImports.size() + failedImports.size(), failedImports.size());
        
        return DepartmentImportResult.builder()
                .successful(successfulImports.size())
                .failed(failedImports.size())
                .message(message)
                .successful_imports(successfulImports)
                .failed_imports(failedImports)
                .build();
    }

    @Override
    public byte[] exportDepartments() {
        List<Department> departments = departmentRepository.findAll();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(CSV_HEADERS))) {
            
            for (Department department : departments) {
                csvPrinter.printRecord(
                        department.getName(),
                        department.getCode(),
                        department.getDescription(),
                        department.getEstablishedDate(),
                        department.isActive()
                );
            }
            
            csvPrinter.flush();
            return out.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to export departments: " + e.getMessage());
        }
    }
    
    private DepartmentResponse mapToDepartmentResponse(Department department) {
        DepartmentResponse.HodDetails hodDetails = null;
        
        if (department.getHodId() != null) {
            User hod = department.getHodId();
            hodDetails = DepartmentResponse.HodDetails.builder()
                    .id(hod.getId())
                    .name(hod.getName())
                    .email(hod.getEmail())
                    .build();
        }
        
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .description(department.getDescription())
                .hod(hodDetails)
                .establishedDate(department.getEstablishedDate())
                .isActive(department.isActive())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
    
    private DepartmentImportResult.FailedImport createFailedImport(CSVRecord record, String error) {
        Map<String, String> departmentMap = new HashMap<>();
        
        record.toMap().forEach((key, value) -> departmentMap.put(key, value));
        
        return DepartmentImportResult.FailedImport.builder()
                .department(departmentMap)
                .error(error)
                .build();
    }
}