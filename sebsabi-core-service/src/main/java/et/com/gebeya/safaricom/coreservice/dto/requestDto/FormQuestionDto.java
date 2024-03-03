package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import et.com.gebeya.safaricom.coreservice.model.MultipleChoiceOption;
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
    private Long id;
    private String questionText;
    private QuestionType questionType;
    private List<MultipleChoiceOption> multipleChoiceOptions;
    private int ratingScale;
    private String optionToUpdate; // Option to update (for multiple-choice questions)
    private String updatedOption;
    //private Form form;
}