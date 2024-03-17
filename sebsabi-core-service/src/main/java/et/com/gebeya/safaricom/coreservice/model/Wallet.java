package et.com.gebeya.safaricom.coreservice.model;
import et.com.gebeya.safaricom.coreservice.dto.WalletDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import java.math.BigDecimal;

@Entity
@Table(name = "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Wallet {
    @jakarta.persistence.Id
    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    @Column(name = "gigworker_id",unique = true)
    private Long gigWorkerId;
    @Column(name = "client_id",unique = true)
    private Long clientId;
    @Column(name = "user_id",unique = true)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    public Wallet(WalletDto walletDto){
        this.setUserId(walletDto.getUserId());
        this.setAmount(walletDto.getAmount());
    }

}
