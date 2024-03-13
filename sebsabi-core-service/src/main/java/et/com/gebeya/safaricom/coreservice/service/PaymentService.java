package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.Exceptions.InsufficientAmountException;
import et.com.gebeya.safaricom.coreservice.Exceptions.PaymentAccountNotFoundException;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.PaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import et.com.gebeya.safaricom.coreservice.model.Payment;
import et.com.gebeya.safaricom.coreservice.repository.PaymentRepository;
import et.com.gebeya.safaricom.coreservice.util.constants.SecurityConstants.MappingUtil;
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

    TransferPaymentResponseDto createPayment(PaymentDto paymentDto){
        Payment payment = MappingUtil.mapBalanceRequestDtoToBalance(paymentDto);
        payment.setAmount(BigDecimal.valueOf(0.0));
        payment = (Payment) paymentRepository.save(payment);
        return MappingUtil.mapBalanceToBalanceResponseDto(payment);
    }

    TransferPaymentResponseDto payingBalance(PaymentDto paymentDto){
        Payment provider = getUser(paymentDto.getUserId());
        if(paymentDto.getBalance().compareTo(BigDecimal.valueOf(100))< 0)
            throw new InsufficientAmountException("You don't have enough Amount to make payment");
        if(provider.getAmount().compareTo(paymentDto.getBalance())<0)
            throw new InsufficientAmountException("Your Balance is Insufficient. Please Add more Amount");
        provider.setAmount(provider.getAmount().subtract(paymentDto.getBalance()));
        return MappingUtil.mapBalanceToBalanceResponseDto((Payment) paymentRepository.save(provider));
            }
    TransferPaymentResponseDto depositBalance(PaymentDto paymentDto){
        Payment client = getUser(paymentDto.getUserId());
        client.setAmount(client.getAmount().add(paymentDto.getBalance()));
        return MappingUtil.mapBalanceToBalanceResponseDto((Payment) paymentRepository.save(driver));
    }
    public TransferPaymentResponseDto transferPaymentFromClientToAdmin(TransferPaymentDto transferPaymentDto) {
        BigDecimal adminCommission = transferPaymentDto.getAmount().multiply(BigDecimal.valueOf(0.1));
        BigDecimal amountAfterCommission = transferPaymentDto.getAmount().subtract(adminCommission);

        // Transfer payment from client account to admin account
        TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
        responseDto.setAmountTransferred(amountAfterCommission);
        responseDto.setAdminCommission(adminCommission);
        responseDto.setMessage("Payment transferred successfully from client to admin.");

        // Assuming you have logic to update admin account balance in the database

        return responseDto;
    }

    public TransferPaymentResponseDto transferPaymentFromAdminToGigWorker(TransferPaymentDto transferPaymentDto) {
        BigDecimal amountToTransfer = transferPaymentDto.getAmount();

        // Transfer payment from admin account to gig worker account
        TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
        responseDto.setAmountTransferred(amountToTransfer);
        responseDto.setMessage("Payment transferred successfully from admin to gig worker.");

        // Assuming you have logic to update gig worker account balance in the database

        return responseDto;
    }

    //    TransferPaymentResponseDto transferPayment(TransferPaymentDto transferPaymentDto){
//        BigDecimal adminCommission = transferPaymentDto.getAmount().multiply(BigDecimal.valueOf(0.1));
//        BigDecimal amountAfterCommission = transferPaymentDto.getAmount().subtract(adminCommission);
//
//        // Transfer payment from client account to admin account
//        Payment adminAccount = getUser(transferPaymentDto.getAdminAccountId());
//        adminAccount.setAmount(adminAccount.getAmount().add(amountAfterCommission));
//        paymentRepository.save(adminAccount);
//
//        // Transfer payment from admin account to gig worker account
//        Payment gigWorkerAccount = getUser(transferPaymentDto.getGigWorkerId());
//        gigWorkerAccount.setAmount(gigWorkerAccount.getAmount().add(amountAfterCommission));
//        paymentRepository.save(gigWorkerAccount);
//
//        // Prepare response
//        TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
//        responseDto.setAmountTransferred(amountAfterCommission);
//        responseDto.setAdminCommission(adminCommission);
//        responseDto.setGigWorkerId(transferPaymentDto.getGigWorkerId());
//        responseDto.setMessage("Payment transferred successfully.");
//        return responseDto;
//    }
    TransferPaymentResponseDto checkBalance(String id){
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

