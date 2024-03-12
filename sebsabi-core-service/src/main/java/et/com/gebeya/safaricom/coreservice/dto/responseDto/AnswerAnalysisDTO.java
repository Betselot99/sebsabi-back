package et.com.gebeya.safaricom.coreservice.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerAnalysisDTO {
    private Map<String, OptionSelectionCountDTO> optionSelectionCount;
    private Double trueFalseAverage;
    private Double rangeAverage;
    private Map<Long, String> textAnswersWithResponseId;
}
