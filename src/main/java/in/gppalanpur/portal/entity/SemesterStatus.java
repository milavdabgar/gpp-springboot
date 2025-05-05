package in.gppalanpur.portal.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterStatus {
    
    public enum Status {
        CLEARED, PENDING, NOT_ATTEMPTED
    }
    
    @Builder.Default
    private Status sem1 = Status.NOT_ATTEMPTED;
    
    @Builder.Default
    private Status sem2 = Status.NOT_ATTEMPTED;
    
    @Builder.Default
    private Status sem3 = Status.NOT_ATTEMPTED;
    
    @Builder.Default
    private Status sem4 = Status.NOT_ATTEMPTED;
    
    @Builder.Default
    private Status sem5 = Status.NOT_ATTEMPTED;
    
    @Builder.Default
    private Status sem6 = Status.NOT_ATTEMPTED;
    
    @Builder.Default
    private Status sem7 = Status.NOT_ATTEMPTED;
    
    @Builder.Default
    private Status sem8 = Status.NOT_ATTEMPTED;
}