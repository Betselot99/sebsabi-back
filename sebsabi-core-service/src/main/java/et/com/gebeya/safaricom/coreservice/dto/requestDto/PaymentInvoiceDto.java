package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInvoiceDto {
    private BigDecimal amount;
    private BigDecimal commission;
    private BigDecimal totalAmount;
}
