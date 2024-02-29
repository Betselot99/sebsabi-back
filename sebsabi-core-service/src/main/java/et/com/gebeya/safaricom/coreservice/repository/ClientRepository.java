package et.com.gebeya.safaricom.coreservice.repository;

import et.com.gebeya.safaricom.coreservice.model.Client;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);
    long countClientsByIsActive(Status status);
}
