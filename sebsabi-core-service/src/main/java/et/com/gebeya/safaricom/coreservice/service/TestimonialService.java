package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.model.Client;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.Testimonial;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import et.com.gebeya.safaricom.coreservice.repository.TestimonialRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestimonialService {
    private final TestimonialRepository testimonialRepository;
    private final ClientService clientService;
    private final FormService formService;
    private final GigWorkerService gigWorkerService;
    public String giveTestimonial(Long clientId, Long formId,Long gigWorkerId, String content) throws AccessDeniedException {
        Client client = clientService.getClientId(clientId).orElseThrow(() -> new EntityNotFoundException("Client not found"));
        GigWorker gigWorker = gigWorkerService.getGigWorkerByIdg(gigWorkerId);
        Form form=formService.getFormForGigWorkerAndClient(formId,client.getId(),gigWorker.getId());
        if(form !=null){
            if(form.getStatus() == Status.Completed){
            Testimonial testimonial = new Testimonial();
            testimonial.setContent(content);
            testimonial.setClient(client);
            testimonial.setGigWorker(gigWorker);

            testimonialRepository.save(testimonial);
            return "Testimonials given successfully for Gig Worker"+gigWorker.getId();
        }else{
                throw new RuntimeException("Form not Completed Yet");
            }

        }else{
            throw new RuntimeException("worker and Client has not worked  Yet");
        }
    }

}
