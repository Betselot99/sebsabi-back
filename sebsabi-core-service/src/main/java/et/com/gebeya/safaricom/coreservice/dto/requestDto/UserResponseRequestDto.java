package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseRequestDto {
    private Long formId;
    private Long questionId;
    private Long gigWorkerId;
    private String userAnswer;
}