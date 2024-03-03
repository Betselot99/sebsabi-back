package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerDto {
    private Long questionId;
    private String answerText;
    private List<Long> selectedOptions; // For multiple choice questions
    private Integer rating; // For range questions
}