package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.*;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.ClientResponse;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.JobFormDisplaydto;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.Proposal;
import et.com.gebeya.safaricom.coreservice.service.ClientService;
import et.com.gebeya.safaricom.coreservice.service.FormService;
import et.com.gebeya.safaricom.coreservice.service.ProposalService;
import et.com.gebeya.safaricom.coreservice.service.UserResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;
import java.util.List;
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
    private final UserResponseService userResponseService;
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
    public ClientResponse updateForm(@RequestBody ClientRequest clientRequest) throws InvocationTargetException, IllegalAccessException {
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

    //add form
    @PostMapping("/create/form")
    @ResponseStatus(HttpStatus.CREATED)
    public JobFormDisplaydto createForm(@RequestBody FormDto formDTO )  {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
        return formService.createForm(formDTO, Long.valueOf((Integer)userId));
    }


    @PostMapping("/create/form/add/question-to-form")
    public Form addQuestionsToForm(@RequestParam Long formID, @RequestBody List<FormQuestionDto> questionDTOList) {
        return formService.addQuestionsToForm(formID, questionDTOList);
    }

//    @GetMapping("/view/form/all-forms/{client_id}")
//    @ResponseStatus(HttpStatus.OK)
//    public Optional<Form> getFormByClientId(@PathVariable Long client_id) {
//        return formService.getFormByClientId(client_id);
//    }
//    @GetMapping("/view/form/status")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Form> getAllFormByClientIdAndStatus(@RequestParam Status status) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Object userId = auth.getPrincipal(); // Get user ID
//        return formService.getFormsByClientIdAndStatus(Long.valueOf((Integer)userId),status);
//    }
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
    public Form updateForm(@RequestParam Long form_id, @RequestBody FormDto formDTO) throws InvocationTargetException, IllegalAccessException {
        return formService.updateForm(form_id, formDTO);
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
}
