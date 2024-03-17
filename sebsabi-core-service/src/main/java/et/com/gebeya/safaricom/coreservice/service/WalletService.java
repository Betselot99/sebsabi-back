package et.com.gebeya.safaricom.coreservice.service;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.WalletCheckDto;
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
    public Wallet addMoneyToWallet(Long userId,BigDecimal amount) {
//        Long userId = getUserIdFromSecurityContext();
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

    @Transactional
    public Wallet addMoneyToClientWallet(Long clientID,BigDecimal amount) {
//        Long userId = getUserIdFromSecurityContext();
        Wallet wallet = walletRepository.findWalletByClientId(clientID);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setClientId(clientID);
            wallet.setAmount(amount);
        } else {
            wallet.setAmount(wallet.getAmount().add(amount));
        }
        return walletRepository.save(wallet);
    }
    @Transactional
    public Wallet addMoneyToGigworkerWallet(Long gigworkerId,BigDecimal amount) {
//        Long userId = getUserIdFromSecurityContext();
        Wallet wallet = walletRepository.findWalletByGigWorkerId(gigworkerId);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setGigWorkerId(gigworkerId);
            wallet.setAmount(amount);
        } else {
            wallet.setAmount(wallet.getAmount().add(amount));
        }
        return walletRepository.save(wallet);
    }
    public WalletCheckDto getClientWallet(Long userId) {
        try {
            return new WalletCheckDto(walletRepository.findWalletByClientId(userId));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format", e);
        } catch (Exception e) {
            throw new RuntimeException("User's wallet not found", e);
        }
    }
    public WalletCheckDto getGigworkerWallet(Long userId) {
        try {
            return new WalletCheckDto(walletRepository.findWalletByGigWorkerId(userId));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format", e);
        } catch (Exception e) {
            throw new RuntimeException("User's wallet not found", e);
        }
    }
    public WalletCheckDto getAdminWallet(Long userId) {
        try {
            return new WalletCheckDto(walletRepository.findByUserId(userId));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format", e);
        } catch (Exception e) {
            throw new RuntimeException("User's wallet not found", e);
        }
    }


//    private Long getUserIdFromSecurityContext() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Object userId = auth.getPrincipal();
//
//        return (Long) userId;
//    }

    public Wallet createNewWallet(Wallet wallet){
        return walletRepository.save(wallet);
    }
}


