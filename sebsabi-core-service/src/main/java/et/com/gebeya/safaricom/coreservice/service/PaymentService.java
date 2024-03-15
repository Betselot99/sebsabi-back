package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.Exceptions.InsufficientAmountException;
import et.com.gebeya.safaricom.coreservice.Exceptions.PaymentAccountNotFoundException;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.PaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.PaymentInvoiceDto;
import et.com.gebeya.safaricom.coreservice.model.*;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
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
import java.util.UUID;

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
        String transactionNumber = generateTransactionNumber();
        Payment payment=new Payment();
        payment.setTransactionNumber(transactionNumber);
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
            if (clientWallet.getAmount().compareTo(BigDecimal.valueOf(amount)) < 0) {
                throw new InsufficientAmountException("Insufficient balance in client's wallet.");
            }
            clientWallet.setAmount(clientWallet.getAmount().subtract(BigDecimal.valueOf(amount)));
            if (adminWallet.getAmount() != null) {
                adminWallet.setAmount(adminWallet.getAmount().add(amountAfterCommission));
            }else{
                adminWallet.setAmount(amountAfterCommission);
            }


            walletRepository.save(clientWallet);
            walletRepository.save(adminWallet);

            // Prepare response
            TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
            responseDto.setAmountTransferred(amountAfterCommission);
            responseDto.setAdminCommission(adminCommission);
            responseDto.setMessage("Payment transferred successfully from client to admin.");
            responseDto.setTransactionNumber(transactionNumber);
            Payment payments = new Payment(responseDto);
            paymentRepository.save(payments);
    //        form.setStatus(Status.Paid);
            formRepository.save(form);
            transferPaymentFromAdminToGigWorker(transferPaymentDto,amountAfterCommission, formId);
            return responseDto;
        }
        throw new RuntimeException("Form Not Found");
    }

    @Transactional
    public TransferPaymentResponseDto transferPaymentFromAdminToGigWorker(TransferPaymentDto transferPaymentDto,BigDecimal amountAfterCommission, Long formId) throws AccessDeniedException {
        Form form = formService.getFormById(formId);
        //Form form2 = formService.getFormForClientByFormId(formId, transferPaymentDto.getClientId());

        if (form != null) {
            // Retrieve sender and receiver wallets
            Wallet adminWallet = getUserWallet(String.valueOf(transferPaymentDto.getAdminId()));
            Wallet gigWorkerWallet = getUserWallet(String.valueOf(form.getAssignedGigWorker().getId()));
            // Perform transaction
            BigDecimal amountToTransfer = amountAfterCommission;
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
            responseDto.setTransactionNumber(generateTransactionNumber());
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

    public TransferPaymentResponseDto checkBalanceForGigWorker(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId);
        TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
        responseDto.setAmountTransferred(wallet.getAmount());
        responseDto.setMessage("Balance checked successfully.");
        return responseDto;
    }

    public TransferPaymentResponseDto checkBalanceForClient(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId);
        TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
        responseDto.setAmountTransferred(wallet.getAmount());
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
    private String generateTransactionNumber() {
        return UUID.randomUUID().toString();
    }
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }
    public PaymentInvoiceDto getPaymentInvoice(TransferPaymentDto transferPaymentDto, Long formId) throws AccessDeniedException{
        Form form = formService.getFormForClientByFormId(formId, transferPaymentDto.getClientId());
        if (form != null) {
            // Perform transaction
            GigWorker assignedGigWorker = form.getAssignedGigWorker();
            Proposal proposal = proposalService.findProposalByFormIdAndGigWorkerId(form.getId(), assignedGigWorker.getId());
            Double amount = proposal.getRatePerForm() * form.getUsageLimit();
            Double commission = amount/10;
            Double totalAmount = amount + commission;
            PaymentInvoiceDto paymentInvoiceDto = PaymentInvoiceDto.builder()
                    .amount(BigDecimal.valueOf(amount))
                    .commission(BigDecimal.valueOf(commission))
                    .totalAmount(BigDecimal.valueOf(totalAmount))
                    .build();

            return paymentInvoiceDto;
        }
        throw new RuntimeException("Form Not Found");
    }
    }

