package et.com.gebeya.safaricom.coreservice.model;
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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

}
