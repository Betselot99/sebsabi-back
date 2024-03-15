package et.com.gebeya.safaricom.coreservice.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerAnalysisDTO {
    private List<QuestionAnalysisDTO> questionAnalysis;
    private byte[] excelReport;

}
