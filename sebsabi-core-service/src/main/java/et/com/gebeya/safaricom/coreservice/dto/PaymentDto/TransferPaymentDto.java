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
    private long clientId;
    private long adminId;
    private BigDecimal amount;

}
