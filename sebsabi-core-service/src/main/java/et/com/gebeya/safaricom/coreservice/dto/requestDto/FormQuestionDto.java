package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import et.com.gebeya.safaricom.coreservice.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormQuestionDto {
    private String questionText;
    private QuestionType questionType;
    private List<String> multipleChoiceOptions;// Include multiple choice options
    private int ratingScale; // Include rating scale from 1 to 5

    //private Form form;
}