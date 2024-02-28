package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.ClientRequest;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormQuestionDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.ProposalDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.ClientResponse;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.JobFormDisplaydto;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.service.ClientService;
import et.com.gebeya.safaricom.coreservice.service.FormService;
import et.com.gebeya.safaricom.coreservice.service.ProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/client")
public class ClientController {
    private final ClientService clientService;
    private final ProposalService proposalService;
    private final FormService formService;

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


    @GetMapping("/view/proposal/{formId}")
    public ProposalDto getProposalByFormId(@PathVariable Long formId) {
        return proposalService.getProposalByFormId(formId);
    }

    @GetMapping("/view/form")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getFormByClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal(); // Get user ID
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


}
