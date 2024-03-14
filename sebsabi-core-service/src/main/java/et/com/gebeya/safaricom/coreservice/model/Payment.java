package et.com.gebeya.safaricom.coreservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Amount is required")
    private BigDecimal amount;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    @Valid
    @JsonIgnore
    private Client client;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "gigworker_id")
    @Valid
    @JsonIgnore
    private GigWorker gigWorker;
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private String transactionNumber;
//payment status, transaction Number, Paid amount
    public Payment(TransferPaymentResponseDto paymentDto){

        this.setAmount(paymentDto.getAmountTransferred());
        this.setTransactionNumber(transactionNumber);
    }
}
