package et.com.gebeya.safaricom.coreservice.repository;

import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    Optional<Form> findByIdAndAssignedGigWorkerId(Long formId, Long gigWorkerId);

    Optional<Form> findFormByClient_Id(Long client_id);

    List<Form> findFormsByClient_Email(String email);

    List<Form> findFormsByStatus(Status status);

    List<Form> findFormsByClient_EmailAndStatus(String email, Status status);


}
