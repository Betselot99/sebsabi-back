package et.com.gebeya.safaricom.coreservice.service;
import et.com.gebeya.safaricom.coreservice.model.Wallet;
import et.com.gebeya.safaricom.coreservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public Wallet addMoneyToWallet(BigDecimal amount) {
        Long userId = getUserIdFromSecurityContext();
        Wallet wallet = walletRepository.findByUserId(userId);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setAmount(amount);
        } else {
            wallet.setAmount(wallet.getAmount().add(amount));
        }
        return walletRepository.save(wallet);
    }

    private Long getUserIdFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();// Adjust this line based on your UserDetails implementation

        return (Long) userId;
    }
}


