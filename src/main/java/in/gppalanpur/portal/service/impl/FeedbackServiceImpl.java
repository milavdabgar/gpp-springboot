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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import in.gppalanpur.portal.dto.feedback.FeedbackAnalysisResponse;
import in.gppalanpur.portal.dto.feedback.FeedbackAnalysisResult;
import in.gppalanpur.portal.dto.feedback.FeedbackBatchResponse;
import in.gppalanpur.portal.dto.feedback.FeedbackImportResult;
import in.gppalanpur.portal.dto.feedback.FeedbackResponse;
import in.gppalanpur.portal.entity.Feedback;
import in.gppalanpur.portal.entity.User;
import in.gppalanpur.portal.exception.ResourceNotFoundException;
import in.gppalanpur.portal.repository.FeedbackRepository;
import in.gppalanpur.portal.repository.UserRepository;
import in.gppalanpur.portal.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    
    @Override
    public Page<FeedbackResponse> getAllFeedback(String year, String term, String branch, Integer semester, Pageable pageable) {
        Page<Feedback> feedbackPage;
        
        if (year != null && term != null && branch != null) {
            feedbackPage = feedbackRepository.findByYearAndTermAndBranch(year, term, branch, pageable);
        } else {
            feedbackPage = feedbackRepository.findAll(pageable);
        }
        
        List<FeedbackResponse> feedbackResponses = feedbackPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(feedbackResponses, pageable, feedbackPage.getTotalElements());
    }

    @Override
    public FeedbackResponse getFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        
        return convertToDto(feedback);
    }

    @Override
    @Transactional
    public FeedbackImportResult importFeedback(MultipartFile file, Long userId) {
        FeedbackImportResult importResult = new FeedbackImportResult();
        String batchId = UUID.randomUUID().toString();
        importResult.setBatchId(batchId);
        
        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
            
            List<CSVRecord> records = csvParser.getRecords();
            importResult.setTotalRecords(records.size());
            
            for (CSVRecord record : records) {
                try {
                    Feedback feedback = parseFeedbackFromCsv(record, batchId, uploader);
                    feedbackRepository.save(feedback);
                    importResult.setSuccessCount(importResult.getSuccessCount() + 1);
                } catch (Exception e) {
                    log.error("Error importing feedback: {}", e.getMessage());
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
    public byte[] exportFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(out), CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Year", "Term", "Branch", "Semester", "Term_Start", "Term_End", 
                        "Subject_Code", "Subject_Name", "Faculty_Name", 
                        "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10", "Q11", "Q12",
                        "Batch_ID", "Created_At")
                .build())) {
            
            for (Feedback feedback : feedbackList) {
                csvPrinter.printRecord(
                        feedback.getId(),
                        feedback.getYear(),
                        feedback.getTerm(),
                        feedback.getBranch(),
                        feedback.getSemester(),
                        feedback.getTermStart(),
                        feedback.getTermEnd(),
                        feedback.getSubjectCode(),
                        feedback.getSubjectName(),
                        feedback.getFacultyName(),
                        feedback.getRatings().getOrDefault("Q1", 0),
                        feedback.getRatings().getOrDefault("Q2", 0),
                        feedback.getRatings().getOrDefault("Q3", 0),
                        feedback.getRatings().getOrDefault("Q4", 0),
                        feedback.getRatings().getOrDefault("Q5", 0),
                        feedback.getRatings().getOrDefault("Q6", 0),
                        feedback.getRatings().getOrDefault("Q7", 0),
                        feedback.getRatings().getOrDefault("Q8", 0),
                        feedback.getRatings().getOrDefault("Q9", 0),
                        feedback.getRatings().getOrDefault("Q10", 0),
                        feedback.getRatings().getOrDefault("Q11", 0),
                        feedback.getRatings().getOrDefault("Q12", 0),
                        feedback.getBatchId(),
                        feedback.getCreatedAt()
                );
            }
            
            csvPrinter.flush();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error exporting feedback to CSV: {}", e.getMessage());
            throw new RuntimeException("Error exporting feedback to CSV", e);
        }
    }

    @Override
    public String getSampleCsv() {
        return "Year,Term,Branch,Sem,Term_Start,Term_End,Subject_Code,Subject_FullName,Faculty_Name,Q1,Q2,Q3,Q4,Q5,Q6,Q7,Q8,Q9,Q10,Q11,Q12\n" +
               "2025,Even,EC,2,24/01/25,10/05/25,DI02000051,Environmental Sustainability,Mr. N J Chauhan,5,5,5,5,5,5,5,5,5,5,5,5";
    }
    
    @Override
    public FeedbackAnalysisResult analyzeFeedback(String year, String term, String branch, Integer semester) {
        FeedbackAnalysisResult result = new FeedbackAnalysisResult();
        
        // Get subject analysis
        List<Object[]> subjectData = feedbackRepository.getSubjectAnalysis(year, term, branch, semester);
        List<FeedbackAnalysisResponse> subjectScores = subjectData.stream()
                .map(data -> {
                    String subjectCode = (String) data[0];
                    String subjectName = (String) data[1];
                    Map<String, Double> scores = new HashMap<>();
                    
                    for (int i = 0; i < 12; i++) {
                        scores.put("Q" + (i + 1), (Double) data[i + 2]);
                    }
                    
                    Double averageScore = scores.values().stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
                    
                    Long count = (Long) data[14];
                    
                    return FeedbackAnalysisResponse.builder()
                            .category("subject")
                            .name(subjectCode + " - " + subjectName)
                            .scores(scores)
                            .count(count.intValue())
                            .averageScore(averageScore)
                            .build();
                })
                .collect(Collectors.toList());
        
        result.setSubjectScores(subjectScores);
        
        // Get faculty analysis
        List<Object[]> facultyData = feedbackRepository.getFacultyAnalysis(year, term, branch, semester);
        List<FeedbackAnalysisResponse> facultyScores = facultyData.stream()
                .map(data -> {
                    String facultyName = (String) data[0];
                    Map<String, Double> scores = new HashMap<>();
                    
                    for (int i = 0; i < 12; i++) {
                        scores.put("Q" + (i + 1), (Double) data[i + 1]);
                    }
                    
                    Double averageScore = scores.values().stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
                    
                    Long count = (Long) data[13];
                    
                    return FeedbackAnalysisResponse.builder()
                            .category("faculty")
                            .name(facultyName)
                            .scores(scores)
                            .count(count.intValue())
                            .averageScore(averageScore)
                            .build();
                })
                .collect(Collectors.toList());
        
        result.setFacultyScores(facultyScores);
        
        // Get semester analysis
        List<Object[]> semesterData = feedbackRepository.getSemesterAnalysis(year, term, branch);
        List<FeedbackAnalysisResponse> semesterScores = semesterData.stream()
                .map(data -> {
                    Integer sem = (Integer) data[0];
                    Map<String, Double> scores = new HashMap<>();
                    
                    for (int i = 0; i < 12; i++) {
                        scores.put("Q" + (i + 1), (Double) data[i + 1]);
                    }
                    
                    Double averageScore = scores.values().stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
                    
                    Long count = (Long) data[13];
                    
                    return FeedbackAnalysisResponse.builder()
                            .category("semester")
                            .name("Semester " + sem)
                            .scores(scores)
                            .count(count.intValue())
                            .averageScore(averageScore)
                            .build();
                })
                .collect(Collectors.toList());
        
        result.setSemesterScores(semesterScores);
        
        // Get branch analysis
        List<Object[]> branchData = feedbackRepository.getBranchAnalysis(year, term);
        List<FeedbackAnalysisResponse> branchScores = branchData.stream()
                .map(data -> {
                    String branchName = (String) data[0];
                    Map<String, Double> scores = new HashMap<>();
                    
                    for (int i = 0; i < 12; i++) {
                        scores.put("Q" + (i + 1), (Double) data[i + 1]);
                    }
                    
                    Double averageScore = scores.values().stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
                    
                    Long count = (Long) data[13];
                    
                    return FeedbackAnalysisResponse.builder()
                            .category("branch")
                            .name(branchName)
                            .scores(scores)
                            .count(count.intValue())
                            .averageScore(averageScore)
                            .build();
                })
                .collect(Collectors.toList());
        
        result.setBranchScores(branchScores);
        
        // Get term-year analysis
        List<Object[]> termYearData = feedbackRepository.getTermYearAnalysis();
        List<FeedbackAnalysisResponse> termYearScores = termYearData.stream()
                .map(data -> {
                    String yearTerm = (String) data[0];
                    Map<String, Double> scores = new HashMap<>();
                    
                    for (int i = 0; i < 12; i++) {
                        scores.put("Q" + (i + 1), (Double) data[i + 1]);
                    }
                    
                    Double averageScore = scores.values().stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
                    
                    Long count = (Long) data[13];
                    
                    return FeedbackAnalysisResponse.builder()
                            .category("yearTerm")
                            .name(yearTerm)
                            .scores(scores)
                            .count(count.intValue())
                            .averageScore(averageScore)
                            .build();
                })
                .collect(Collectors.toList());
        
        result.setTermYearScores(termYearScores);
        
        // Calculate correlation matrix
        result.setCorrelationMatrix(calculateCorrelationMatrix(subjectScores, facultyScores));
        
        return result;
    }
    
    @Override
    public byte[] generatePdfReport(String year, String term, String branch, Integer semester) {
        FeedbackAnalysisResult analysis = analyzeFeedback(year, term, branch, semester);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add title
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 18, com.lowagie.text.Font.BOLD);
            Paragraph title = new Paragraph("Feedback Analysis Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Add filters info
            Paragraph filters = new Paragraph("Filters: " + 
                    (year != null ? "Year: " + year + ", " : "") +
                    (term != null ? "Term: " + term + ", " : "") +
                    (branch != null ? "Branch: " + branch + ", " : "") +
                    (semester != null ? "Semester: " + semester : ""));
            document.add(filters);
            document.add(new Paragraph(" "));
            
            // Add subject analysis
            document.add(new Paragraph("Subject Analysis", FontFactory.getFont(FontFactory.HELVETICA, 14, com.lowagie.text.Font.BOLD)));
            document.add(new Paragraph(" "));
            
            PdfPTable subjectTable = new PdfPTable(15);
            subjectTable.setWidthPercentage(100);
            
            // Add table headers
            addTableHeader(subjectTable, "Subject", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", 
                    "Q7", "Q8", "Q9", "Q10", "Q11", "Q12", "Average", "Count");
            
            // Add subject data
            for (FeedbackAnalysisResponse subject : analysis.getSubjectScores()) {
                addTableRow(subjectTable, subject.getName(),
                        String.format("%.2f", subject.getScores().get("Q1")),
                        String.format("%.2f", subject.getScores().get("Q2")),
                        String.format("%.2f", subject.getScores().get("Q3")),
                        String.format("%.2f", subject.getScores().get("Q4")),
                        String.format("%.2f", subject.getScores().get("Q5")),
                        String.format("%.2f", subject.getScores().get("Q6")),
                        String.format("%.2f", subject.getScores().get("Q7")),
                        String.format("%.2f", subject.getScores().get("Q8")),
                        String.format("%.2f", subject.getScores().get("Q9")),
                        String.format("%.2f", subject.getScores().get("Q10")),
                        String.format("%.2f", subject.getScores().get("Q11")),
                        String.format("%.2f", subject.getScores().get("Q12")),
                        String.format("%.2f", subject.getAverageScore()),
                        subject.getCount().toString());
            }
            
            document.add(subjectTable);
            document.add(new Paragraph(" "));
            
            // Add faculty analysis
            document.add(new Paragraph("Faculty Analysis", FontFactory.getFont(FontFactory.HELVETICA, 14, com.lowagie.text.Font.BOLD)));
            document.add(new Paragraph(" "));
            
            PdfPTable facultyTable = new PdfPTable(15);
            facultyTable.setWidthPercentage(100);
            
            // Add table headers
            addTableHeader(facultyTable, "Faculty", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", 
                    "Q7", "Q8", "Q9", "Q10", "Q11", "Q12", "Average", "Count");
            
            // Add faculty data
            for (FeedbackAnalysisResponse faculty : analysis.getFacultyScores()) {
                addTableRow(facultyTable, faculty.getName(),
                        String.format("%.2f", faculty.getScores().get("Q1")),
                        String.format("%.2f", faculty.getScores().get("Q2")),
                        String.format("%.2f", faculty.getScores().get("Q3")),
                        String.format("%.2f", faculty.getScores().get("Q4")),
                        String.format("%.2f", faculty.getScores().get("Q5")),
                        String.format("%.2f", faculty.getScores().get("Q6")),
                        String.format("%.2f", faculty.getScores().get("Q7")),
                        String.format("%.2f", faculty.getScores().get("Q8")),
                        String.format("%.2f", faculty.getScores().get("Q9")),
                        String.format("%.2f", faculty.getScores().get("Q10")),
                        String.format("%.2f", faculty.getScores().get("Q11")),
                        String.format("%.2f", faculty.getScores().get("Q12")),
                        String.format("%.2f", faculty.getAverageScore()),
                        faculty.getCount().toString());
            }
            
            document.add(facultyTable);
            document.close();
            
            return baos.toByteArray();
        } catch (DocumentException e) {
            log.error("Error generating PDF report: {}", e.getMessage());
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
    
    @Override
    public byte[] generateExcelReport(String year, String term, String branch, Integer semester) {
        FeedbackAnalysisResult analysis = analyzeFeedback(year, term, branch, semester);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create styles
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // Create subject sheet
            Sheet subjectSheet = workbook.createSheet("Subjects");
            Row headerRow = subjectSheet.createRow(0);
            
            // Create headers
            String[] headers = {"Subject", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", 
                    "Q7", "Q8", "Q9", "Q10", "Q11", "Q12", "Average", "Count"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Add subject data
            int rowNum = 1;
            for (FeedbackAnalysisResponse subject : analysis.getSubjectScores()) {
                Row row = subjectSheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(subject.getName());
                row.createCell(1).setCellValue(subject.getScores().get("Q1"));
                row.createCell(2).setCellValue(subject.getScores().get("Q2"));
                row.createCell(3).setCellValue(subject.getScores().get("Q3"));
                row.createCell(4).setCellValue(subject.getScores().get("Q4"));
                row.createCell(5).setCellValue(subject.getScores().get("Q5"));
                row.createCell(6).setCellValue(subject.getScores().get("Q6"));
                row.createCell(7).setCellValue(subject.getScores().get("Q7"));
                row.createCell(8).setCellValue(subject.getScores().get("Q8"));
                row.createCell(9).setCellValue(subject.getScores().get("Q9"));
                row.createCell(10).setCellValue(subject.getScores().get("Q10"));
                row.createCell(11).setCellValue(subject.getScores().get("Q11"));
                row.createCell(12).setCellValue(subject.getScores().get("Q12"));
                row.createCell(13).setCellValue(subject.getAverageScore());
                row.createCell(14).setCellValue(subject.getCount());
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                subjectSheet.autoSizeColumn(i);
            }
            
            // Create faculty sheet
            Sheet facultySheet = workbook.createSheet("Faculty");
            headerRow = facultySheet.createRow(0);
            
            // Create headers
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Add faculty data
            rowNum = 1;
            for (FeedbackAnalysisResponse faculty : analysis.getFacultyScores()) {
                Row row = facultySheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(faculty.getName());
                row.createCell(1).setCellValue(faculty.getScores().get("Q1"));
                row.createCell(2).setCellValue(faculty.getScores().get("Q2"));
                row.createCell(3).setCellValue(faculty.getScores().get("Q3"));
                row.createCell(4).setCellValue(faculty.getScores().get("Q4"));
                row.createCell(5).setCellValue(faculty.getScores().get("Q5"));
                row.createCell(6).setCellValue(faculty.getScores().get("Q6"));
                row.createCell(7).setCellValue(faculty.getScores().get("Q7"));
                row.createCell(8).setCellValue(faculty.getScores().get("Q8"));
                row.createCell(9).setCellValue(faculty.getScores().get("Q9"));
                row.createCell(10).setCellValue(faculty.getScores().get("Q10"));
                row.createCell(11).setCellValue(faculty.getScores().get("Q11"));
                row.createCell(12).setCellValue(faculty.getScores().get("Q12"));
                row.createCell(13).setCellValue(faculty.getAverageScore());
                row.createCell(14).setCellValue(faculty.getCount());
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                facultySheet.autoSizeColumn(i);
            }
            
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error generating Excel report: {}", e.getMessage());
            throw new RuntimeException("Error generating Excel report", e);
        }
    }
    
    @Override
    public List<FeedbackBatchResponse> getUploadBatches(Pageable pageable) {
        List<Object[]> batchData = feedbackRepository.findUploadBatches(pageable);
        
        return batchData.stream()
                .map(data -> {
                    String batchId = (String) data[0];
                    Long count = (Long) data[1];
                    LocalDateTime uploadedAt = (LocalDateTime) data[2];
                    
                    return FeedbackBatchResponse.builder()
                            .batchId(batchId)
                            .count(count.intValue())
                            .uploadedAt(uploadedAt)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int deleteFeedbackByBatch(String batchId) {
        List<Feedback> feedbackList = feedbackRepository.findByBatchId(batchId);
        int count = feedbackList.size();
        
        feedbackRepository.deleteAll(feedbackList);
        return count;
    }

    @Override
    @Transactional
    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        
        feedbackRepository.delete(feedback);
    }
    
    private Feedback parseFeedbackFromCsv(CSVRecord record, String batchId, User uploader) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        
        Map<String, Integer> ratings = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            ratings.put("Q" + i, Integer.parseInt(record.get("Q" + i)));
        }
        
        return Feedback.builder()
                .year(record.get("Year"))
                .term(record.get("Term"))
                .branch(record.get("Branch"))
                .semester(Integer.parseInt(record.get("Sem")))
                .termStart(LocalDate.parse(record.get("Term_Start"), dateFormatter))
                .termEnd(LocalDate.parse(record.get("Term_End"), dateFormatter))
                .subjectCode(record.get("Subject_Code"))
                .subjectName(record.get("Subject_FullName"))
                .facultyName(record.get("Faculty_Name"))
                .ratings(ratings)
                .uploadedBy(uploader)
                .batchId(batchId)
                .build();
    }
    
    private FeedbackResponse convertToDto(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .year(feedback.getYear())
                .term(feedback.getTerm())
                .branch(feedback.getBranch())
                .semester(feedback.getSemester())
                .termStart(feedback.getTermStart())
                .termEnd(feedback.getTermEnd())
                .subjectCode(feedback.getSubjectCode())
                .subjectName(feedback.getSubjectName())
                .facultyName(feedback.getFacultyName())
                .ratings(feedback.getRatings())
                .uploadedById(feedback.getUploadedBy() != null ? feedback.getUploadedBy().getId() : null)
                .uploadedByName(feedback.getUploadedBy() != null ? feedback.getUploadedBy().getName() : null)
                .batchId(feedback.getBatchId())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }
    
    private Map<String, Map<String, Double>> calculateCorrelationMatrix(List<FeedbackAnalysisResponse> subjects, List<FeedbackAnalysisResponse> faculties) {
        Map<String, Map<String, Double>> correlationMatrix = new HashMap<>();
        
        // For simplicity, we'll just create a mock correlation matrix
        // In a real implementation, this would calculate Pearson correlation coefficients
        for (FeedbackAnalysisResponse subject : subjects) {
            Map<String, Double> correlations = new HashMap<>();
            
            for (FeedbackAnalysisResponse faculty : faculties) {
                // Generate a random correlation between -1 and 1
                double correlation = Math.random() * 2 - 1;
                correlations.put(faculty.getName(), correlation);
            }
            
            correlationMatrix.put(subject.getName(), correlations);
        }
        
        return correlationMatrix;
    }
    
    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new java.awt.Color(220, 220, 220));
            cell.setPadding(5);
            cell.setPhrase(new Paragraph(header, FontFactory.getFont(FontFactory.HELVETICA, 10, com.lowagie.text.Font.BOLD)));
            table.addCell(cell);
        }
    }
    
    private void addTableRow(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell();
            cell.setPadding(5);
            cell.setPhrase(new Paragraph(value, FontFactory.getFont(FontFactory.HELVETICA, 10)));
            table.addCell(cell);
        }
    }
}
