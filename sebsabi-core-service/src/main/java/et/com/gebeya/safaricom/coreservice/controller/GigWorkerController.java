package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.GigWorkerRequest;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.ProposalDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.GigwWorkerResponse;
import et.com.gebeya.safaricom.coreservice.service.GigWorkerService;
import et.com.gebeya.safaricom.coreservice.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/gig-worker")
public class GigWorkerController {
    private final GigWorkerService gigWorkerService;
    private final ProposalService proposalService;
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
    @PostMapping("/submit")
    public ResponseEntity<String> submitProposal(@RequestBody ProposalDto proposalDto) {

        proposalService.submitProposal(proposalDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Proposal submitted successfully");
    }
}
