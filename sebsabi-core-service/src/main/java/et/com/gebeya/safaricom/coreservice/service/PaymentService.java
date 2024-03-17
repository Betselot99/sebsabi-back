package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.Exceptions.InsufficientAmountException;
import et.com.gebeya.safaricom.coreservice.Exceptions.PaymentAccountNotFoundException;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.PaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.WalletCheckDto;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final FormService formService;
    private final ProposalService proposalService;
    private final FormRepository formRepository;
    private final ClientService clientService;
    private final WalletRepository walletRepository;
    private final GigWorkerService gigWorkerService;
    @Transactional
    public TransferPaymentResponseDto transferPaymentFromClientToAdmin(TransferPaymentDto transferPaymentDto, Long formId) throws AccessDeniedException {
        Form form = formService.getFormForClientByFormId(formId, transferPaymentDto.getClientId());
        String transactionNumber = generateTransactionNumber();
        Payment payment=new Payment();
        payment.setTransactionNumber(transactionNumber);
        if (form != null&&form.getStatus()==Status.Completed) {
            Optional<Client> client=clientService.getClientId(transferPaymentDto.getClientId());
            // Retrieve sender and receiver wallets
            Wallet clientWallet = walletRepository.findWalletByClientId(transferPaymentDto.getClientId());
            Wallet adminWallet = walletRepository.findByUserId(transferPaymentDto.getAdminId());

            // Perform transaction
            GigWorker assignedGigWorker = form.getAssignedGigWorker();
            Proposal proposal = proposalService.findProposalByFormIdAndGigWorkerId(form.getId(), assignedGigWorker.getId());
            Double actualAmount = proposal.getRatePerForm()*form.getUsageLimit();
            transferPaymentDto.setAmount(BigDecimal.valueOf(actualAmount));
            BigDecimal adminCommission = transferPaymentDto.getAmount().multiply(BigDecimal.valueOf(0.1));
            BigDecimal amountAfterCommission = transferPaymentDto.getAmount().add(adminCommission);
            if (clientWallet.getAmount().compareTo(amountAfterCommission) < 0) {
                throw new InsufficientAmountException("Insufficient balance in client's wallet.");
            }
            clientWallet.setAmount(clientWallet.getAmount().subtract(amountAfterCommission));
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
            responseDto.setClientId(clientWallet.getClientId());
            responseDto.setMessage("Payment transferred successfully from client to admin.");
            responseDto.setTransactionNumber(transactionNumber);

            form.setStatus(Status.Paid);
            formRepository.save(form);
            transferPaymentFromAdminToGigWorker(responseDto,BigDecimal.valueOf(actualAmount),adminCommission, formId);
            return responseDto;
            }
        throw new RuntimeException("Form is Already paid");
    }

    @Transactional
    public TransferPaymentResponseDto transferPaymentFromAdminToGigWorker(TransferPaymentResponseDto transferPaymentResponseDto,BigDecimal amount,BigDecimal adminCommission, Long formId) throws AccessDeniedException {
        Form form = formService.getFormById(formId);
        Optional<Client> client=clientService.getClientId(transferPaymentResponseDto.getClientId());
        if(client.isPresent()){
            Client client1=client.get();


        if (form != null) {
            Wallet adminWallet = walletRepository.findByUserId(0L);
            Wallet gigWorkerWallet = walletRepository.findWalletByGigWorkerId(form.getAssignedGigWorker().getId());
            GigWorker gigWorker=gigWorkerService.getGigWorkerByIdg(form.getAssignedGigWorker().getId());
            // Perform transaction
            BigDecimal adminWalletBalance = new BigDecimal(String.valueOf(adminWallet.getAmount())); // Get the current balance
            BigDecimal amountAfterCommissionDecimal =amount.subtract(adminCommission); // Convert amountAfterCommission to BigDecimal
            if (adminWalletBalance.compareTo(amountAfterCommissionDecimal) < 0) {
                throw new InsufficientAmountException("Insufficient balance in admin's wallet.");
            }
            // Deduct from admin wallet and add to gig worker wallet
            adminWallet.setAmount(adminWalletBalance.subtract(amountAfterCommissionDecimal));
            gigWorkerWallet.setAmount(gigWorkerWallet.getAmount().add(amountAfterCommissionDecimal));

            walletRepository.save(adminWallet);
            walletRepository.save(gigWorkerWallet);

            // Prepare response
            TransferPaymentResponseDto responseDto = new TransferPaymentResponseDto();
            responseDto.setAmountTransferred(amountAfterCommissionDecimal);
            responseDto.setGigWorkerId(gigWorkerWallet.getGigWorkerId());
            responseDto.setTransactionNumber(generateTransactionNumber());
            responseDto.setMessage("Payment transferred successfully from admin to gig worker.");

            // Save payment details
            Payment payments = new Payment();
            payments.setAmount(amount.add(adminCommission)); // Set the amount
            payments.setAdminCommission(adminCommission.multiply(BigDecimal.valueOf(2)));
            payments.setTransactionNumber(responseDto.getTransactionNumber()); // Set transaction number
            payments.setGigWorker(gigWorker);
            payments.setClient(client1);
            paymentRepository.save(payments);

            // Update form status and return response
            formRepository.save(form);
            return responseDto;
        }
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

