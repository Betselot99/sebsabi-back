package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import et.com.gebeya.safaricom.coreservice.model.Status;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormDto {
    @NonNull
    private String title;
    @NonNull
    private String description;
    //private List<FormQuestionDto> questions;
    @NonNull
    private int usageLimit;
    @NonNull
    private Status status;
    private List<FormQuestionDto> formQuestions; // Add this field for managing FormQuestionDtos

}