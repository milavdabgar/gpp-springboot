package in.gppalanpur.portal.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import in.gppalanpur.portal.dto.result.ResultAnalysisResponse;
import in.gppalanpur.portal.dto.result.ResultBatchResponse;
import in.gppalanpur.portal.dto.result.ResultImportResult;
import in.gppalanpur.portal.dto.result.ResultResponse;
import in.gppalanpur.portal.dto.result.ResultSubjectResponse;
import in.gppalanpur.portal.entity.Result;
import in.gppalanpur.portal.entity.ResultSubject;
import in.gppalanpur.portal.exception.ResourceNotFoundException;
import in.gppalanpur.portal.repository.ResultRepository;
import in.gppalanpur.portal.service.ResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    
    @Override
    public Page<ResultResponse> getAllResults(Pageable pageable) {
        Page<Result> resultsPage = resultRepository.findAll(pageable);
        List<ResultResponse> resultResponses = resultsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(resultResponses, pageable, resultsPage.getTotalElements());
    }

    @Override
    public ResultResponse getResult(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with id: " + id));
        
        return convertToDto(result);
    }

    @Override
    public Page<ResultResponse> getStudentResults(String enrollmentNo, Pageable pageable) {
        List<Result> results = resultRepository.findByEnrollmentNo(enrollmentNo);
        
        List<ResultResponse> resultResponses = results.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), resultResponses.size());
        
        List<ResultResponse> pageContent = resultResponses.subList(start, end);
        return new PageImpl<>(pageContent, pageable, resultResponses.size());
    }

    @Override
    @Transactional
    public ResultImportResult importResults(MultipartFile file, Long userId) {
        ResultImportResult importResult = new ResultImportResult();
        String batchId = UUID.randomUUID().toString();
        importResult.setBatchId(batchId);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
            
            List<CSVRecord> records = csvParser.getRecords();
            importResult.setTotalRecords(records.size());
            
            for (CSVRecord record : records) {
                try {
                    Result result = parseResultFromCsv(record, batchId);
                    resultRepository.save(result);
                    importResult.setSuccessCount(importResult.getSuccessCount() + 1);
                } catch (Exception e) {
                    log.error("Error importing result: {}", e.getMessage());
                    importResult.setErrorCount(importResult.getErrorCount() + 1);
                    importResult.getErrors().add("Row " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            log.error("Error reading CSV file: {}", e.getMessage());
            importResult.getErrors().add("Error reading CSV file: " + e.getMessage());
        }
        
        return importResult;
    }

    @Override
    public byte[] exportResults() {
        List<Result> results = resultRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(out), CSVFormat.DEFAULT.builder()
                .setHeader("ID", "ST_ID", "Enrollment_No", "Extype", "Exam_ID", "Exam", 
                        "Declaration_Date", "Academic_Year", "Semester", "Unit_No", "Exam_Number",
                        "Name", "Inst_Code", "Inst_Name", "Course_Name", "Branch_Code", "Branch_Name",
                        "Total_Credits", "Earned_Credits", "SPI", "CPI", "CGPA", "Result", "Trials", "Upload_Batch")
                .build())) {
            
            for (Result result : results) {
                csvPrinter.printRecord(
                        result.getId(),
                        result.getStId(),
                        result.getEnrollmentNo(),
                        result.getExtype(),
                        result.getExamId(),
                        result.getExam(),
                        result.getDeclarationDate(),
                        result.getAcademicYear(),
                        result.getSemester(),
                        result.getUnitNo(),
                        result.getExamNumber(),
                        result.getName(),
                        result.getInstCode(),
                        result.getInstName(),
                        result.getCourseName(),
                        result.getBranchCode(),
                        result.getBranchName(),
                        result.getTotalCredits(),
                        result.getEarnedCredits(),
                        result.getSpi(),
                        result.getCpi(),
                        result.getCgpa(),
                        result.getResult(),
                        result.getTrials(),
                        result.getUploadBatch()
                );
            }
            
            csvPrinter.flush();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error exporting results to CSV: {}", e.getMessage());
            throw new RuntimeException("Error exporting results to CSV", e);
        }
    }

    @Override
    public List<ResultAnalysisResponse> getBranchAnalysis(String academicYear, Integer examId) {
        List<Object[]> analysisData = resultRepository.getBranchAnalysis(academicYear, examId);
        
        return analysisData.stream()
                .map(data -> {
                    String branchName = (String) data[0];
                    Integer semester = (Integer) data[1];
                    Double avgSpi = (Double) data[2];
                    Long passCount = (Long) data[3];
                    Long totalCount = (Long) data[4];
                    
                    Double passPercentage = totalCount > 0 ? (passCount * 100.0) / totalCount : 0.0;
                    
                    return ResultAnalysisResponse.builder()
                            .branchName(branchName)
                            .semester(semester)
                            .averageSpi(avgSpi)
                            .passCount(passCount.intValue())
                            .totalCount(totalCount.intValue())
                            .passPercentage(passPercentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ResultBatchResponse> getUploadBatches(Pageable pageable) {
        List<Object[]> batchData = resultRepository.findUploadBatches(pageable);
        
        return batchData.stream()
                .map(data -> {
                    String batchId = (String) data[0];
                    Long count = (Long) data[1];
                    LocalDateTime uploadedAt = (LocalDateTime) data[2];
                    
                    return ResultBatchResponse.builder()
                            .batchId(batchId)
                            .count(count.intValue())
                            .uploadedAt(uploadedAt)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int deleteResultsByBatch(String batchId) {
        List<Result> results = resultRepository.findByUploadBatch(batchId);
        int count = results.size();
        
        resultRepository.deleteAll(results);
        return count;
    }

    @Override
    @Transactional
    public void deleteResult(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with id: " + id));
        
        resultRepository.delete(result);
    }
    
    private Result parseResultFromCsv(CSVRecord record, String batchId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        Result result = Result.builder()
                .stId(record.get("ST_ID"))
                .enrollmentNo(record.get("Enrollment_No"))
                .extype(record.get("Extype"))
                .examId(Integer.parseInt(record.get("Exam_ID")))
                .exam(record.get("Exam"))
                .declarationDate(LocalDate.parse(record.get("Declaration_Date"), dateFormatter))
                .academicYear(record.get("Academic_Year"))
                .semester(Integer.parseInt(record.get("Semester")))
                .unitNo(Double.parseDouble(record.get("Unit_No")))
                .examNumber(Double.parseDouble(record.get("Exam_Number")))
                .name(record.get("Name"))
                .instCode(Integer.parseInt(record.get("Inst_Code")))
                .instName(record.get("Inst_Name"))
                .courseName(record.get("Course_Name"))
                .branchCode(Integer.parseInt(record.get("Branch_Code")))
                .branchName(record.get("Branch_Name"))
                .totalCredits(Double.parseDouble(record.get("Total_Credits")))
                .earnedCredits(Double.parseDouble(record.get("Earned_Credits")))
                .spi(Double.parseDouble(record.get("SPI")))
                .cpi(Double.parseDouble(record.get("CPI")))
                .cgpa(Double.parseDouble(record.get("CGPA")))
                .result(record.get("Result"))
                .trials(Integer.parseInt(record.get("Trials")))
                .uploadBatch(batchId)
                .build();
        
        // Parse subjects (assuming they are in a format like Subject1_Code, Subject1_Name, etc.)
        List<ResultSubject> subjects = new ArrayList<>();
        int subjectIndex = 1;
        
        while (record.isMapped("Subject" + subjectIndex + "_Code")) {
            String code = record.get("Subject" + subjectIndex + "_Code");
            String name = record.get("Subject" + subjectIndex + "_Name");
            Double credits = Double.parseDouble(record.get("Subject" + subjectIndex + "_Credits"));
            String grade = record.get("Subject" + subjectIndex + "_Grade");
            Boolean isBacklog = "Y".equalsIgnoreCase(record.get("Subject" + subjectIndex + "_Backlog"));
            
            ResultSubject subject = ResultSubject.builder()
                    .result(result)
                    .code(code)
                    .name(name)
                    .credits(credits)
                    .grade(grade)
                    .isBacklog(isBacklog)
                    .build();
            
            // Add detailed grades if available
            if (record.isMapped("Subject" + subjectIndex + "_Theory_ESE")) {
                subject.setTheoryEseGrade(record.get("Subject" + subjectIndex + "_Theory_ESE"));
            }
            if (record.isMapped("Subject" + subjectIndex + "_Theory_PA")) {
                subject.setTheoryPaGrade(record.get("Subject" + subjectIndex + "_Theory_PA"));
            }
            if (record.isMapped("Subject" + subjectIndex + "_Theory_Total")) {
                subject.setTheoryTotalGrade(record.get("Subject" + subjectIndex + "_Theory_Total"));
            }
            if (record.isMapped("Subject" + subjectIndex + "_Practical_PA")) {
                subject.setPracticalPaGrade(record.get("Subject" + subjectIndex + "_Practical_PA"));
            }
            if (record.isMapped("Subject" + subjectIndex + "_Practical_Viva")) {
                subject.setPracticalVivaGrade(record.get("Subject" + subjectIndex + "_Practical_Viva"));
            }
            if (record.isMapped("Subject" + subjectIndex + "_Practical_Total")) {
                subject.setPracticalTotalGrade(record.get("Subject" + subjectIndex + "_Practical_Total"));
            }
            
            subjects.add(subject);
            subjectIndex++;
        }
        
        result.setSubjects(subjects);
        return result;
    }
    
    private ResultResponse convertToDto(Result result) {
        ResultResponse dto = ResultResponse.builder()
                .id(result.getId())
                .stId(result.getStId())
                .enrollmentNo(result.getEnrollmentNo())
                .extype(result.getExtype())
                .examId(result.getExamId())
                .exam(result.getExam())
                .declarationDate(result.getDeclarationDate())
                .academicYear(result.getAcademicYear())
                .semester(result.getSemester())
                .unitNo(result.getUnitNo())
                .examNumber(result.getExamNumber())
                .name(result.getName())
                .instCode(result.getInstCode())
                .instName(result.getInstName())
                .courseName(result.getCourseName())
                .branchCode(result.getBranchCode())
                .branchName(result.getBranchName())
                .totalCredits(result.getTotalCredits())
                .earnedCredits(result.getEarnedCredits())
                .spi(result.getSpi())
                .cpi(result.getCpi())
                .cgpa(result.getCgpa())
                .result(result.getResult())
                .trials(result.getTrials())
                .uploadBatch(result.getUploadBatch())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
        
        List<ResultSubjectResponse> subjectResponses = result.getSubjects().stream()
                .map(subject -> ResultSubjectResponse.builder()
                        .id(subject.getId())
                        .code(subject.getCode())
                        .name(subject.getName())
                        .credits(subject.getCredits())
                        .grade(subject.getGrade())
                        .isBacklog(subject.getIsBacklog())
                        .theoryEseGrade(subject.getTheoryEseGrade())
                        .theoryPaGrade(subject.getTheoryPaGrade())
                        .theoryTotalGrade(subject.getTheoryTotalGrade())
                        .practicalPaGrade(subject.getPracticalPaGrade())
                        .practicalVivaGrade(subject.getPracticalVivaGrade())
                        .practicalTotalGrade(subject.getPracticalTotalGrade())
                        .build())
                .collect(Collectors.toList());
        
        dto.setSubjects(subjectResponses);
        return dto;
    }
}
