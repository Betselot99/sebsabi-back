package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.Exceptions.PaymentAccountNotFoundException;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.PaymentDto;
import et.com.gebeya.safaricom.coreservice.model.Payment;
import et.com.gebeya.safaricom.coreservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    PaymentResponseDto createPayment(PaymentDto paymentDto){
        Payment payment = MappingUtil.mapBalanceRequestDtoBalance(paymentDto);
        payment.setAmount(BigDecimal.valueOf(0.0));
        payment = (Payment) paymentRepository.save(payment);
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
    PamentResponseDto transferPayment(TransferPaymentDto transferPaymentDto){
        Payment driver = getUser(transferPaymentDto.getDriverId());
        Payment provider = getUser(transferPaymentDto.getProviderId());
        BigDecimal updatedDriverBalance = drover.getAmount().subtract(transferPaymentDto.getAmount());
        if(updatedDriverBalance.compareTo(BigDecimal.ZERO)<0)
            throw new InsufficientAmount("Your Balance is Insufficient, please Add amount");
        driver.setAmount(updatedDriverBalance);
        BigDecimal updatedProviderBalance = provider.getAmount().add(transferPaymentDto.getAmount());
        provider.setAmount(updatedProviderBalance);
        paymentRepository.save(provider);
        return MappingUtil.mapBalanceToBalanceResponseDto(paymentRepository.save(driver));
    }
    PamentResponseDto checkBalance(String id){
        Payment payment = getUser(id);
        return MappingUtil.mapBalanceToBalanceResponseDto(payment);
    }
    private Payment getUser(String id){
        List<Payment> user = paymentRepository.findAll();
        if(user.isEmpty())
            throw new PaymentAccountNotFoundException("Your Account isn't available currently");
        return user.get(0);
    }
    Map<String,String> deleteUser(String id) {
        Payment user = getUser(id);
        paymentRepository.delete(user);
        return Map.of("message", "User Payment Account Deleted Successfully");
    }
    }

