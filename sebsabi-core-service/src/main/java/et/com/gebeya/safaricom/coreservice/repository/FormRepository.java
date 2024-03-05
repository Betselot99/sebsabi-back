package et.com.gebeya.safaricom.coreservice.repository;

import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.Status;
import jakarta.ws.rs.core.EntityPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    Optional<Form> findFormByIdAndAssignedGigWorkerId(Long id,Long gig_worker_id);
    Optional<Form> findFormByIdAndClient_Id(Long id, Long client_id);
    Optional<Form> findFormByClient_Id(Long client_id);

    List<Form> findFormsByAssignedGigWorkerIdAndStatus(Long gig_worker_id,Status status);


    List<Form> findFormsByStatus(Status status);

    List<Form> findFormsByClient_IdAndStatus(Long client_id, Status status);


    @Query("SELECT f.status, COUNT(f) FROM Form f GROUP BY f.status")
    List<Object[]> countFormsByStatus();

    @Query("SELECT f.id, COUNT(p) FROM Form f LEFT JOIN f.proposals p GROUP BY f.id")
    List<Object[]> countProposalsPerForm();
    @Query("SELECT gw.id, COUNT(f) FROM Form f JOIN f.assignedGigWorker gw GROUP BY gw.id")
    List<Object[]> countFormsAssignedToGigWorkers();
    @Query("SELECT c.id, COUNT(f) FROM Form f JOIN f.client c GROUP BY c.id")
    List<Object[]> countFormsPerClient();
    @Query("SELECT c.id, COUNT(f) FROM Client c LEFT JOIN c.forms f WHERE f.status = :status GROUP BY c.id")
    List<Object[]> countFormsPerClientByStatus(@Param("status") Status status);
}
