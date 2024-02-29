package et.com.gebeya.safaricom.coreservice.repository;

import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GigWorkerRepository extends JpaRepository<GigWorker,Long> {
    long countGigWorkersByIsActive(Status status);
}
