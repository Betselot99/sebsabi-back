package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.model.Client;
import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.Testimonial;
import et.com.gebeya.safaricom.coreservice.repository.TestimonialRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class TestimonialService {
    private final TestimonialRepository testimonialRepository;
    private final ClientService clientService;
    private final GigWorkerService gigWorkerService;
    public Testimonial giveTestimonial(Long clientId, Long gigWorkerId, String content) {
        Client client = clientService.getClientId(clientId).orElseThrow(() -> new EntityNotFoundException("Client not found"));
        GigWorker gigWorker = gigWorkerService.getGigWorkerByIdg(gigWorkerId);

        Testimonial testimonial = new Testimonial();
        testimonial.setContent(content);
        testimonial.setClient(client);
        testimonial.setGigWorker(gigWorker);

        return testimonialRepository.save(testimonial);
    }

}
