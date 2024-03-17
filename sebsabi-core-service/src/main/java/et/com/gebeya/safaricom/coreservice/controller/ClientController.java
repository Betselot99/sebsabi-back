package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.Exceptions.FormNotFoundException;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.WalletCheckDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.*;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.AnswerAnalysisDTO;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.ClientResponse;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.JobFormDisplaydto;
import et.com.gebeya.safaricom.coreservice.model.*;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import et.com.gebeya.safaricom.coreservice.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/core/client")
public class ClientController {
    private final ClientService clientService;
    private final ProposalService proposalService;
    private final FormService formService;
    private final FormQuestionService formQuestionService;
    private final UserResponseService userResponseService;
    private final TestimonialService testimonialService;
    private final AnswerService answerAnalysisService;
    private final WalletService walletService;
    private final PaymentService paymentService;
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
//   @CircuitBreaker(name = "identity",fallbackMethod = "fallBackMethod")
//   @TimeLimiter(name = "identity")
//   @Retry(name = "identity")
    public CompletableFuture<String> createClients(@Valid @RequestBody ClientRequest clientRequest) {
        return CompletableFuture.supplyAsync(() -> clientService.createClients(clientRequest));
    }

    public CompletableFuture<String> fallBackMethod(ClientRequest clientRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong , please Try signing up after some time.");
    }


    @GetMapping("/view/profile")
    @ResponseStatus(HttpStatus.OK)
    public ClientResponse getClientById(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
        return clientService.getClientById(Long.valueOf((Integer)userId));
    }
    @PutMapping("/view/profile/update")
    @ResponseStatus(HttpStatus.OK)
    public ClientResponse updateProfile(@RequestBody ClientRequest clientRequest) throws InvocationTargetException, IllegalAccessException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
        return clientService.updateClient(Long.valueOf((Integer)userId), clientRequest);
    }
    @GetMapping("/status/{formId}")
    public ResponseEntity<Long> getJobStatus(@PathVariable("formId") long formId) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        Long progress = userResponseService.jobStatusForClient(formId,Long.valueOf((Integer)userId));
        return new ResponseEntity<>(progress, HttpStatus.OK);
    }

    @GetMapping("/view/form")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getFormByClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
        log.info(userId.toString());
        Optional<Form> form = formService.getFormByClientId(Long.valueOf((Integer) userId));

        if (form.isPresent()) {
            return ResponseEntity.ok(form.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No form created");
        }
    }
    @GetMapping("/search/form")
    public ResponseEntity<Page<Form>> searchForms(
            @RequestParam Map<String, String> requestParams,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
        FormSearchRequestDto searchRequestDto = new FormSearchRequestDto(requestParams);
        Page<Form> forms = formService.searchClientForms(searchRequestDto,Long.valueOf((Integer)userId),pageable);
        return new ResponseEntity<>(forms, HttpStatus.OK);
    }

    //add form
    @PostMapping("/create/form")
    @ResponseStatus(HttpStatus.CREATED)
    public JobFormDisplaydto createForm(@RequestBody FormDto formDTO )  {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
        return formService.createForm(formDTO, Long.valueOf((Integer)userId));
    }

    @PostMapping("/create/form/add/question-to-form")
    public ResponseEntity<Form> addQuestionsToForm(@RequestParam Long formID,@RequestBody List<FormQuestionDto> questionDTOs) {
        Form form = formService.addQuestionsToForm(formID, questionDTOs);
        return new ResponseEntity<>(form, HttpStatus.CREATED);
    }

    @GetMapping("/view/form/status")
    @ResponseStatus(HttpStatus.OK)
    public List<Form> getAllFormByClientIdAndStatus(@RequestParam Status status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
        return formService.getFormsByClientIdAndStatus(Long.valueOf((Integer)userId),status);
    }


    @PutMapping("/view/form/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Form> updateForm(@RequestParam Long formId, @RequestBody @Valid FormDto formDto) throws InvocationTargetException, IllegalAccessException {
        Form updatedForm = formService.updateForm(formId, formDto);
        return ResponseEntity.ok(updatedForm);
    }

    @PutMapping("/view/form/update/questions")
    public ResponseEntity<?> updateFormQuestions(@RequestParam Long formId, @RequestBody List<FormQuestionDto> formQuestionDtoList) {
        try {
            List<FormQuestion> updatedQuestions = formQuestionService.updateFormQuestions(formId, formQuestionDtoList);
            return ResponseEntity.ok(updatedQuestions);
        } catch (FormNotFoundException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.startsWith("Form not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage); // Return 404 with specific message
            } else {
                return ResponseEntity.notFound().build(); // Return 404 with generic message
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // Return 500 with exception message
        }
    }


    @GetMapping("/view/form/proposal/{formId}")
    public ResponseEntity<List<Proposal>> getProposalsByFormId(@PathVariable Long formId) {
        List<Proposal> proposals = proposalService.getProposalsByFormId(formId);
        return ResponseEntity.ok(proposals);
    }

    @PostMapping("/view/form/proposal/accept/{proposalId}")
    public ResponseEntity<String> acceptProposal(@PathVariable Long proposalId) {
        proposalService.acceptProposal(proposalId);
        return ResponseEntity.ok("Proposal accepted successfully");
    }

    @GetMapping("/view/form/progress/{formId}") // Modified mapping to make it unique
    public ResponseEntity<Long> getClientJobStatus(@PathVariable("formId") long formId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object userId = auth.getPrincipal();
            Long progress = userResponseService.jobStatusForClient(formId, Long.valueOf((Integer)userId));
            return new ResponseEntity<>(progress, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }



    @GetMapping(value = "/view/form/completed/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnswerAnalysisDTO> analyzeAnswers(@RequestParam Long formId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object userId = auth.getPrincipal();
            AnswerAnalysisDTO analysisDTO = answerAnalysisService.analyzeAnswers(formId, Long.valueOf((Integer)userId));
            return ResponseEntity.ok(analysisDTO);
        } catch (IOException e) {
            // Handle IO Exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
//    @GetMapping(value = "/view/form/completed/analyze/download", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ByteArrayResource> analyzeAnswersExcelDownload(@RequestParam Long formId) {
//        try {
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            Object userId = auth.getPrincipal();
//            AnswerAnalysisDTO analysisDTO = answerAnalysisService.analyzeAnswers(formId, Long.valueOf((Integer)userId));
//            byte[] excelReport = analysisDTO.getExcelReport();
//
//            // Set the content type and disposition of the response
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDispositionFormData("filename", "analysis_report.xlsx");
//
//            // Return the Excel file as a byte array resource
//            ByteArrayResource resource = new ByteArrayResource(excelReport);
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(resource);
//        } catch (IOException e) {
//            // Handle IOException
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @PostMapping("/view/forms/completed/giveTestimonials")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> giveTestimonialForGigWorker(@RequestParam Long formId,
                                                              @RequestParam Long gigWorkerId,
                                                              @RequestBody Map<String, String> requestBody)
            throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();

        String testimonialContent = requestBody.get("testimonial");

        testimonialService.giveTestimonial(Long.valueOf((Integer)userId), formId, gigWorkerId, testimonialContent);

        return ResponseEntity.status(HttpStatus.CREATED).body("Testimonial submitted successfully");
    }
    @GetMapping("/check/invoice")
    public ResponseEntity<PaymentInvoiceDto> getPaymentInvoice(@RequestParam Long formId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        try {
            TransferPaymentDto transferPaymentDto = new TransferPaymentDto();
            transferPaymentDto.setClientId(Long.valueOf((Integer)userId));
            //transferPaymentDto.setAdminId(0);
            PaymentInvoiceDto invoiceDto = paymentService.getPaymentInvoice(transferPaymentDto,formId);
            return new ResponseEntity<>(invoiceDto, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            // Handle access denied exception
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    @PostMapping("/pay")
    public ResponseEntity<TransferPaymentResponseDto> transferFromClientToAdmin(@RequestParam Long formId) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        TransferPaymentDto transferPaymentDto = new TransferPaymentDto();
        transferPaymentDto.setAdminId(0);
        transferPaymentDto.setClientId(Long.valueOf((Integer) userId));
        TransferPaymentResponseDto response = paymentService.transferPaymentFromClientToAdmin(transferPaymentDto, formId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/check/wallet")
    public ResponseEntity<WalletCheckDto> checkBalanceForClient(){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object userId = auth.getPrincipal();
            WalletCheckDto responseDto = walletService.getClientWallet(Long.valueOf((Integer)userId));
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    @PostMapping("/check/wallet/add-money")
    public ResponseEntity<Wallet> addMoneyToWallet(@RequestParam BigDecimal amount) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        Wallet wallet = walletService.addMoneyToClientWallet(Long.valueOf((Integer)userId),amount);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }
}
