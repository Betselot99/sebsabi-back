package et.com.gebeya.safaricom.coreservice.dto.PaymentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferPaymentDto {
    private String driverId;
    private String providerId;
    private BigDecimal amount;

}
