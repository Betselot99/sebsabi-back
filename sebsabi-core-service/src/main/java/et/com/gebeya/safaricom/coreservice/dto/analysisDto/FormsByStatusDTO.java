package et.com.gebeya.safaricom.coreservice.dto.analysisDto;

import et.com.gebeya.safaricom.coreservice.model.Status;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormsByStatusDTO {
    private Status status;
    private long count;

    // Constructors, getters, and setters
}