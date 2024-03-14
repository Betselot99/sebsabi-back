package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.ProposalDto;
import et.com.gebeya.safaricom.coreservice.service.TestimonialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/testimonials")
public class TestimonialController {
    private final TestimonialService testimonialService;

    @PostMapping("/giveTestimonials")
    public ResponseEntity<String> giveTestimonialForGigWorker(@RequestParam Long clientId,@RequestParam Long formId,@RequestParam Long gigWorkerId ,@RequestBody String testimonial) throws AccessDeniedException {
        testimonialService.giveTestimonial(clientId,formId,gigWorkerId,testimonial);
        return ResponseEntity.status(HttpStatus.CREATED).body("Testimonial submitted successfully");
    }
}
