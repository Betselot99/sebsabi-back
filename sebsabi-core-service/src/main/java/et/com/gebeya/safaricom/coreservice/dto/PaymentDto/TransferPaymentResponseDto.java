package et.com.gebeya.safaricom.coreservice.dto.PaymentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferPaymentResponseDto {
    private BigDecimal amountTransferred;
    private BigDecimal adminCommission;
    private String gigWorkerId;
    private String message;
}
