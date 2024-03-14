package et.com.gebeya.safaricom.coreservice.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnalysisDTO {
    private Long id;
    private String questionText;
    private String questionType;
    private List<MultipleChoiceOptionDTO> multipleChoiceOptions;
    private Integer ratingScale;
    private Long trueCount; // Only applicable for TRUE_FALSE questions
    private Long falseCount; // Only applicable for TRUE_FALSE questions
    private Map<String, Long> optionSelectionCount; // Only applicable for MULTIPLE_CHOICE questions
    private Double averageRating; // Only applicable for RATING_SCALE questions
    private Map<Long, String> textAnswersWithResponseId; // Only applicable for TEXT questions
    private byte[] excelReport;
}