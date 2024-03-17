package et.com.gebeya.safaricom.coreservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletDto {

    private Long userId;
    private BigDecimal amount;
}
