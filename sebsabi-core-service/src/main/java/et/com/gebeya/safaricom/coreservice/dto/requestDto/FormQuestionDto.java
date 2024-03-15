package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import et.com.gebeya.safaricom.coreservice.model.MultipleChoiceOption;
import et.com.gebeya.safaricom.coreservice.model.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "Question text is required")
    private String questionText;
    @NotNull(message = "Question type is required")
    private QuestionType questionType;
    private List<String> multipleChoiceOptions;
    private int ratingScale;
    private String optionToUpdate; // Option to update (for multiple-choice questions)
    private String updatedOption;
    //private Form form;
}