package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.Exceptions.InsufficientAmountException;
import et.com.gebeya.safaricom.coreservice.Exceptions.PaymentAccountNotFoundException;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.PaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import et.com.gebeya.safaricom.coreservice.model.*;
import et.com.gebeya.safaricom.coreservice.repository.FormRepository;
import et.com.gebeya.safaricom.coreservice.repository.PaymentRepository;
import et.com.gebeya.safaricom.coreservice.repository.WalletRepository;
import et.com.gebeya.safaricom.coreservice.util.constants.SecurityConstants.MappingUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final FormService formService;
    private final ProposalService proposalService;
    private final FormRepository formRepository;
    private final WalletRepository walletRepository;
    @Transactional
    public TransferPaymentResponseDto transferPaymentFromClientToAdmin(TransferPaymentDto transferPaymentDto, Long formId) throws AccessDeniedException {
        Form form = formService.getFormForClientByFormId(formId, transferPaymentDto.getClientId());
        if (form != null) {
            // Retrieve sender and receiver wallets
            Wallet clientWallet = getUserWallet(String.valueOf(transferPaymentDto.getClientId()));
            Wallet adminWallet = getUserWallet(String.valueOf(transferPaymentDto.getAdminId()));

            // Perform transaction
            GigWorker assignedGigWorker = form.getAssignedGigWorker();
            Proposal proposal = proposalService.findProposalByFormIdAndGigWorkerId(form.getId(), assignedGigWorker.getId());
            Double amount = proposal.getRatePerForm()*form.getUsageLimit();
            transferPaymentDto.setAmount(BigDecimal.valueOf(amount));
            BigDecimal adminCommission = transferPaymentDto.getAmount().multiply(BigDecimal.valueOf(0.1));
            BigDecimal amountAfterCommission = transferPaymentDto.getAmount().subtract(adminCommission);
            form.setStatus(Status.Paid);
            if (clientWallet.getAmount().compareTo(BigDecimal.valueOf(amount)) < 0) {
                throw new InsufficientAmountException("Insufficient balance in client's wallet.");
            }
            clientWallet.setAmount(clientWallet.getAmount().subtract(BigDecimal.valueOf(amount)));
            adminWallet.setAmount(adminWallet.getAmount().add(amountAfterCommission));
            walletRepository.save(clientWallet);
            walletRepository.save(adminWallet);

            // Prepare response
            TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
            responseDto.setAmountTransferred(amountAfterCommission);
            responseDto.setAdminCommission(adminCommission);
            responseDto.setMessage("Payment transferred successfully from client to admin.");
            return responseDto;
        }
        throw new RuntimeException("Form Not Found");
    }

    @Transactional
    public TransferPaymentResponseDto transferPaymentFromAdminToGigWorker(TransferPaymentDto transferPaymentDto, Long formId) {
        Form form = formService.getFormById(formId);
        if (form != null) {
            // Retrieve sender and receiver wallets
            Wallet adminWallet = getUserWallet(String.valueOf(transferPaymentDto.getAdminId()));
            Wallet gigWorkerWallet = getUserWallet(String.valueOf(form.getAssignedGigWorker().getId()));

            // Perform transaction
            BigDecimal amountToTransfer = transferPaymentDto.getAmount();
            if (adminWallet.getAmount().compareTo(amountToTransfer) < 0) {
                throw new InsufficientAmountException("Insufficient balance in admin's wallet.");
            }
            adminWallet.setAmount(adminWallet.getAmount().subtract(amountToTransfer));
            gigWorkerWallet.setAmount(gigWorkerWallet.getAmount().add(amountToTransfer));
            walletRepository.save(adminWallet);
            walletRepository.save(gigWorkerWallet);

            // Prepare response
            TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
            responseDto.setAmountTransferred(amountToTransfer);
            responseDto.setMessage("Payment transferred successfully from admin to gig worker.");
            responseDto.setGigWorkerId(form.getAssignedGigWorker().getId());
            responseDto.setClientId(form.getClient().getId());
            return responseDto;
        }
        throw new RuntimeException("Form Not Found");
    }

    private Wallet getUserWallet(String userId) {
        try {
            return walletRepository.findByUserId(Long.valueOf(userId));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format", e);
        } catch (Exception e) {
            throw new RuntimeException("User's wallet not found", e);
        }
    }

    public TransferPaymentResponseDto checkBalanceForGigWorker(String userId) {
        GigWorker assignedGigWorker = new GigWorker();
        Payment payment = getUser(String.valueOf(assignedGigWorker.getId()));
        TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
        responseDto.setAmountTransferred(payment.getAmount());
        responseDto.setMessage("Balance checked successfully.");
        return responseDto;
    }

    public TransferPaymentResponseDto checkBalanceForClient(String userId) {
        Client assignedClient = new Client();
        Payment payment = getUser(String.valueOf(assignedClient.getId()));
        TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
        responseDto.setAmountTransferred(payment.getAmount());
        responseDto.setMessage("Balance checked successfully.");
        return responseDto;
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
//    @Transactional
//    public TransferPaymentResponseDto transferPaymentFromClientToAdmin(TransferPaymentDto transferPaymentDto, Long formId) throws AccessDeniedException {
//    Form form = formService.getFormForClientByFormId(formId, transferPaymentDto.getClientId());
//    if(form != null){
//        GigWorker assignedGigWorker = form.getAssignedGigWorker();
//        Proposal proposal = proposalService.findProposalByFormIdAndGigWorkerId(form.getId(), assignedGigWorker.getId());
//        Double amount = proposal.getRatePerForm()*form.getUsageLimit();
//        transferPaymentDto.setAmount(BigDecimal.valueOf(amount));
//        BigDecimal adminCommission = transferPaymentDto.getAmount().multiply(BigDecimal.valueOf(0.1));
//        BigDecimal amountAfterCommission = transferPaymentDto.getAmount().subtract(adminCommission);
//        form.setStatus(Status.Paid);
//        // Transfer payment from client account to admin account
//        TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
//        responseDto.setAmountTransferred(amountAfterCommission);
//        responseDto.setAdminCommission(adminCommission);
//        responseDto.setMessage("Payment transferred successfully from client to admin.");
//        Payment payment = new Payment(responseDto);
//        paymentRepository.save(payment);
//        formRepository.save(form);
//
//        transferPaymentFromAdminToGigWorker(transferPaymentDto, formId);
//        return responseDto;
//    }
//    throw new RuntimeException("Form Not Found");
//    }
//@Transactional
//    public TransferPaymentResponseDto transferPaymentFromAdminToGigWorker(TransferPaymentDto transferPaymentDto, Long formId) {
//       Form form = formService.getFormById(formId);
//       if (form.getStatus()==Status.Paid){
//           BigDecimal amountToTransfer = transferPaymentDto.getAmount();
//
//           // Transfer payment from admin account to gig worker account
//           TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
//           responseDto.setAmountTransferred(amountToTransfer);
//           responseDto.setMessage("Payment transferred successfully from admin to gig worker.");
//           responseDto.setGigWorkerId(form.getAssignedGigWorker().getId());
//           responseDto.setClientId(form.getClient().getId());
//           Payment payment = new Payment(responseDto);
//           paymentRepository.save(payment);
//           return responseDto;
//       }
//throw new RuntimeException("Failed to transfer");
//    }

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
//    TransferPaymentResponseDto checkBalance(String id){
//        Payment payment = getUser(id);
//        return MappingUtil.mapBalanceToBalanceResponseDto(payment);
//    }

    }

