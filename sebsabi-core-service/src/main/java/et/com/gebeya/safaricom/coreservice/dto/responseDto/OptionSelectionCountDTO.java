package et.com.gebeya.safaricom.coreservice.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionSelectionCountDTO {
    private String optionId;
    private Long count;

}
