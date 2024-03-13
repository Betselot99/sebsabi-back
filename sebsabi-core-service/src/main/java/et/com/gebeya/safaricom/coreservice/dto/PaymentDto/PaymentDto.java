package et.com.gebeya.safaricom.coreservice.dto.PaymentDto;

import lombok.*;

import java.math.BigDecimal;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private long userId;
    private BigDecimal balance;

}
