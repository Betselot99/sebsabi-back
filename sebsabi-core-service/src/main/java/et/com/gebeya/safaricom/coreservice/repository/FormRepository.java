package et.com.gebeya.safaricom.coreservice.repository;

import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.Status;
import jakarta.ws.rs.core.EntityPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    Optional<Form> findByIdAndAssignedGigWorkerId(Long formId, Long gigWorkerId);

    Optional<Form> findFormByIdAndAssignedGigWorkerId(Long id,Long gig_worker_id);
    Optional<Form> findFormByClient_Id(Long client_id);

    List<Form> findFormsByClient_Email(String email);

    List<Form> findFormsByStatus(Status status);

    List<Form> findFormsByClient_IdAndStatus(Long client_id, Status status);


    Page<Form> findAll(Specification<Form> specification, Pageable pageable);

}
