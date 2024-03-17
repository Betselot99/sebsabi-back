package et.com.gebeya.identityservice.service;
import et.com.gebeya.identityservice.entity.Wallet;
import et.com.gebeya.identityservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public Wallet createNewWallet(Wallet wallet){
        return walletRepository.save(wallet);
    }
}


