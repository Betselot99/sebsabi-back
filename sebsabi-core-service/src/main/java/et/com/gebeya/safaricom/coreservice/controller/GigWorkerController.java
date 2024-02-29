package et.com.gebeya.safaricom.coreservice.controller;


import et.com.gebeya.safaricom.coreservice.dto.requestDto.GigWorkerRequest;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.ProposalDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.GigwWorkerResponse;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.FormQuestion;
import et.com.gebeya.safaricom.coreservice.model.Status;
import et.com.gebeya.safaricom.coreservice.model.UserResponse;
import et.com.gebeya.safaricom.coreservice.service.*;
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
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/core/gig-worker")
public class GigWorkerController {
    private final GigWorkerService gigWorkerService;
    private final ProposalService proposalService;
    private final FormService formService;
    private final FormQuestionService formQuestionService;
    private final UserResponseService userResponseService;
   @PostMapping("/signup")
//   @CircuitBreaker(name = "identity",fallbackMethod = "fallBackMethod")
//   @TimeLimiter(name = "identity")
//   @Retry(name = "identity")
   @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<String> createGigWorker(@RequestBody GigWorkerRequest gigWorkerRequest){
      return CompletableFuture.supplyAsync(()->gigWorkerService.createGigWorkers(gigWorkerRequest));
   }
    public CompletableFuture<String> fallBackMethod(GigWorkerRequest gigWorkerRequest, RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(()->"Oops! Something went wrong , please Try signing up after some time.");
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
    @GetMapping("/view/forms")
    @ResponseStatus(HttpStatus.OK)
    public List<Form> getAllFormByStatus() {
        Status status = Status.Posted; // Set the status to "Posted"
        return formService.getFormsByStatus(status);
    }
    @GetMapping("/forms/view/questionOfForm")
    public List<FormQuestion> viewQuestions(@RequestParam Long formID) throws InvocationTargetException, IllegalAccessException, AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userId = auth.getPrincipal();
        log.info(userId.toString());
       return formQuestionService.getFormQuestionBYFOrmID(formID,Long.valueOf((Integer)userId));
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

}
