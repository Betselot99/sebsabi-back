package et.com.gebeya.safaricom.coreservice.repository;

import et.com.gebeya.safaricom.coreservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
