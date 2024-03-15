package et.com.gebeya.safaricom.coreservice.dto.analysisDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormsPerClientDTO {
    private Long clientId;
    private long count;

    // Constructors, getters, and setters
}