package et.com.gebeya.safaricom.coreservice.dto.PaymentDto;

import lombok.*;

import java.math.BigDecimal;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private String userId;
    private BigDecimal balance;

}
