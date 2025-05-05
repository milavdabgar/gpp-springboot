package in.gppalanpur.portal.dto.department;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentRequest {
    
    @Size(max = 100)
    private String name;
    
    @Size(max = 10)
    private String code;
    
    private String description;
    
    private Long hodId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate establishedDate;
    
    private Boolean isActive;
}