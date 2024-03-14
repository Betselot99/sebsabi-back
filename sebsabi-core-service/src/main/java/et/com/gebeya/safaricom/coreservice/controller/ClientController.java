package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.Exceptions.FormNotFoundException;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.*;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.AnswerAnalysisDTO;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.ClientResponse;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.JobFormDisplaydto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.OptionSelectionCountDTO;
import et.com.gebeya.safaricom.coreservice.model.*;
import et.com.gebeya.safaricom.coreservice.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
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
    private final AnswerService answerService;
    private final PaymentService paymentService;
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
//   @CircuitBreaker(name = "identity",fallbackMethod = "fallBackMethod")
//   @TimeLimiter(name = "identity")
//   @Retry(name = "identity")
//   @RolesAllowed("CLIENTS")
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

//    @GetMapping("/view/form/all-forms/{client_id}")
//    @ResponseStatus(HttpStatus.OK)
//    public Optional<Form> getFormByClientId(@PathVariable Long client_id) {
//        return formService.getFormByClientId(client_id);
//    }
    @GetMapping("/view/form/status")
    @ResponseStatus(HttpStatus.OK)
    public List<Form> getAllFormByClientIdAndStatus(@RequestParam Status status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
        return formService.getFormsByClientIdAndStatus(Long.valueOf((Integer)userId),status);
    }
//@GetMapping("/view/forms/by_params")
//public ResponseEntity<List<Form>> getForms(@RequestParam(required = false) Map<String, String> search,
//                                           @PageableDefault(page = 0, size = 10) Pageable pageable) {
//    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//    Object userId = auth.getPrincipal();
//    FormSearchRequestDto formSearch = new FormSearchRequestDto(search);
//    formSearch.setClientId(Long.valueOf((Integer) userId));
//    return formService.getForms(formSearch, pageable); // Return the ResponseEntity directly
//}
//


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

    @GetMapping("/view/form/status/{formId}") // Modified mapping to make it unique
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

    @GetMapping("/view/form/completed/analyze")
    public AnswerAnalysisDTO analyzeAnswers(@RequestParam Long formId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object userId = auth.getPrincipal();
            // Analyze answers for the provided form ID
            return answerService.analyzeAnswers(formId,Long.valueOf((Integer)userId));
        } catch (FormNotFoundException ex) {
            // Handle the form not found exception
            throw new FormNotFoundException(ex.getMessage());
        } catch (AccessDeniedException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/pay")
    public ResponseEntity<TransferPaymentResponseDto> transferFromClientToAdmin(@RequestParam Long formId) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        TransferPaymentDto transferPaymentDto = new TransferPaymentDto();
        transferPaymentDto.setClientId(Long.valueOf((Integer)userId));
        transferPaymentDto.setAdminId(0);
        TransferPaymentResponseDto response = paymentService.transferPaymentFromClientToAdmin(transferPaymentDto, formId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/check/wallet")
public ResponseEntity<TransferPaymentResponseDto> checkBalanceForClient(@RequestParam Long clientId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        TransferPaymentDto transferPaymentDto = new TransferPaymentDto();
        transferPaymentDto.setClientId((Long) userId);
        TransferPaymentResponseDto responseDto = paymentService.checkBalanceForClient(String.valueOf(clientId));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
}
}
