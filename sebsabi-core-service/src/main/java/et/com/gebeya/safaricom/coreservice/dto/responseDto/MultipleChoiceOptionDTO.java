package et.com.gebeya.safaricom.coreservice.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleChoiceOptionDTO {
    private Long id;
    private String optionText;
}
