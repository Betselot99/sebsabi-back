package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.model.Payment;
import org.springframework.web.bind.annotation.Mapping;

import java.math.BigDecimal;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    PaymentResponseDto createPayment(PaymentDto paymentDto){
        Payment payment = MappingUtil.mapBalanceRequestDtoBalance(paymentDto);
        payment.setAmount(BigDecimal.valueOf(0.0));
        payment = paymentRepository.save(payment);
        return MappingUtil.mapBalanceToBalanceResponseDto(payment);
    }

    PaymentResponseDto payingBalance(PaymentDto paymentDto){
        Payment provider = getUser(paymentDto.getUserId());
        if(paymentDto.getBalance().compareTo(BigDecimal.valueOf(100))< 0)
            throw new InsufficientAmount("You don't have enough Amount to make payment");
        if(provider.getAmount().compareTo(paymentDto.getBalance())<0)
            throw new InsufficientAmount("Your Balance is Insufficient. Please Add more Amount");
        provider.setAmount(provider.getAmount().subtract(paymentDto.getBalance()));
        return MappingUtil.mpaBalanceToBalanceResponseDto(paymentRepository.save(provider));
            }
    PamentResponseDto depositBalance(PaymentDto paymentDto){
        Payment driver = getUser(paymentDto.getUserId());
        driver.setAmount(driver.getAmount().add(paymentDto.getBalance()));
        return MappingUtil.mapBalanceToBalanceResponseDto(paymentRepository.save(driver));
    }
    }

