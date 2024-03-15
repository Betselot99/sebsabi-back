package et.com.gebeya.safaricom.coreservice.dto.analysisDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProposalsPerFormDTO {
    private Long formId;
    private long count;

}
