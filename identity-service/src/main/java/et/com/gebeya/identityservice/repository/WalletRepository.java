package et.com.gebeya.identityservice.repository;

import et.com.gebeya.identityservice.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByUserId(Long userId);
    Wallet findWalletByGigWorkerId(Long gigworker_id);

    Wallet findWalletByClientId(Long client_id);
}
