package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestimonialRequestDto {

    private Long formId;
    private Long gigWorkerId;
    private String testimonials;
}
