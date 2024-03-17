package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.WalletDto;
import et.com.gebeya.safaricom.coreservice.model.Wallet;
import et.com.gebeya.safaricom.coreservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/add-money")
    public ResponseEntity<Wallet> addMoneyToWallet(@RequestBody WalletDto walletDto) {
        Wallet wallet=new Wallet(walletDto);
        Wallet wallets = walletService.createNewWallet(wallet);
        return new ResponseEntity<>(wallets, HttpStatus.OK);
    }
}
