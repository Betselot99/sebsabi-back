package et.com.gebeya.safaricom.coreservice.repository;

import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GigWorkerRepository extends JpaRepository<GigWorker,Long> {
    long countGigWorkersByIsActive(Status status);

    Page<GigWorker> findAll(Specification<GigWorker> spec, Pageable pageable);
}
