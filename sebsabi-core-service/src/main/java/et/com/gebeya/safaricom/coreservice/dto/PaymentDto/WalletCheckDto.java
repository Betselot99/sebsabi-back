package et.com.gebeya.safaricom.coreservice.dto.PaymentDto;

import et.com.gebeya.safaricom.coreservice.model.Wallet;
import et.com.gebeya.safaricom.coreservice.service.WalletService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletCheckDto {
    private BigDecimal amount;
    public WalletCheckDto(Wallet wallet){
        this.amount=wallet.getAmount();
    }
}
