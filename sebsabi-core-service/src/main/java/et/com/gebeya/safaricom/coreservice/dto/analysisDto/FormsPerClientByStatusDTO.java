package et.com.gebeya.safaricom.coreservice.dto.analysisDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormsPerClientByStatusDTO {
    private Long clientId;
    private long count;
}
