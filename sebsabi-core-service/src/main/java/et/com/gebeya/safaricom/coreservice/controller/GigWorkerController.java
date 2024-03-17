package et.com.gebeya.safaricom.coreservice.controller;


import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.WalletCheckDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormSearchRequestDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.GigWorkerRequest;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.ProposalDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.UserResponseRequestDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.FormGigworkerDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.GigwWorkerResponse;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.Wallet;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import et.com.gebeya.safaricom.coreservice.model.UserResponse;
import et.com.gebeya.safaricom.coreservice.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/gig-worker")
public class GigWorkerController {
    private final GigWorkerService gigWorkerService;
    private final ProposalService proposalService;
    private final FormService formService;
    private final UserResponseService userResponseService;
    private final PaymentService paymentService;
    private final WalletService walletService;
    @PostMapping("/signup")
//   @CircuitBreaker(name = "identity",fallbackMethod = "fallBackMethod")
//   @TimeLimiter(name = "identity")
//   @Retry(name = "identity")
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<String> createGigWorker(@RequestBody GigWorkerRequest gigWorkerRequest) {
        return CompletableFuture.supplyAsync(() -> gigWorkerService.createGigWorkers(gigWorkerRequest));
    }

    public CompletableFuture<String> fallBackMethod(GigWorkerRequest gigWorkerRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong , please Try signing up after some time.");
    }

    @GetMapping("/view/profile")
    @ResponseStatus(HttpStatus.OK)
    public GigwWorkerResponse getGigworkerById(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
        return gigWorkerService.getGigWorkerById(Long.valueOf((Integer)userId));
    }
    @PutMapping("/view/profile/update")
    @ResponseStatus(HttpStatus.OK)
    public GigwWorkerResponse updateGigworker(@RequestBody GigWorkerRequest gigWorkerRequest) throws InvocationTargetException, IllegalAccessException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
        return gigWorkerService.updateGigworker(Long.valueOf((Integer)userId), gigWorkerRequest);
    }
    @GetMapping("/search")
    public ResponseEntity<Page<Form>> searchForms(
            @RequestParam Map<String, String> requestParams,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        FormSearchRequestDto searchRequestDto = new FormSearchRequestDto(requestParams);
        Page<Form> forms = formService.searchForms(searchRequestDto, pageable);
        return new ResponseEntity<>(forms, HttpStatus.OK);
    }

    @GetMapping("/view/forms")
    @ResponseStatus(HttpStatus.OK)
    public List<Form> getAllFormByStatus() {
        Status status = Status.Posted; // Set the status to "Posted"
        return formService.getFormsByStatus(status);
    }

    @GetMapping("/view/forms/claimed")
    @ResponseStatus(HttpStatus.OK)
    public List<Form> getAllFormByClaimed() {
        Status status = Status.Claimed; // Set the status to "Claimed"
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        return formService.getFormsByGigWorkerIdAndStatus(Long.valueOf((Integer)userId),status);
    }

    @GetMapping("/view/forms/applied")
    @ResponseStatus(HttpStatus.OK)
    public List<Form> getAllFormByApplied() {
        Status status = Status.Claimed; // Set the status to "Applied"
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        return formService.getFormsByGigWorkerIdAndStatus(Long.valueOf((Integer)userId),status);
    }
    @GetMapping("/view/forms/completed")
    @ResponseStatus(HttpStatus.OK)
    public List<Form> getAllFormByCompleted() {
        Status status = Status.Completed; // Set the status to "Completed"
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        return formService.getFormsByGigWorkerIdAndStatus(Long.valueOf((Integer)userId),status);
    }

    @PostMapping("/view/forms/proposal/submit")
    public ResponseEntity<String> submitProposal(@RequestParam Long form_id ,@RequestBody ProposalDto proposalDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        proposalDto.setFormId(form_id);
        proposalDto.setGigWorkerId(Long.valueOf((Integer)userId));
        proposalService.submitProposal(proposalDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Proposal submitted successfully");
    }

    @PostMapping("/view/forms/formQuestion/submit-response")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse submitResponse(@RequestBody UserResponseRequestDto responseDTO) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        responseDTO.setGigWorkerId(Long.valueOf((Integer)userId));
        return userResponseService.submitResponse(responseDTO);
    }

    @GetMapping("/view/form/status/{formId}")
    public ResponseEntity<Long> getGigWorkerJobStatus(@PathVariable("formId") long formId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object userId = auth.getPrincipal();
            Long progress = userResponseService.jobStatusForGigWorker(formId,Long.valueOf((Integer)userId));
            return new ResponseEntity<>(progress, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/check/balance")
    public ResponseEntity<WalletCheckDto> checkBalanceForGigWoker() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        WalletCheckDto responseDto = walletService.getGigworkerWallet(Long.valueOf((Integer)userId));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }
    @PostMapping("/check/wallet/add-money")
    public ResponseEntity<Wallet> addMoneyToWallet(@RequestParam BigDecimal amount) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        Wallet wallet = walletService.addMoneyToGigworkerWallet(Long.valueOf((Integer)userId),amount);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }

}
