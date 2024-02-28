package et.com.gebeya.safaricom.coreservice.service;


import et.com.gebeya.safaricom.coreservice.Exceptions.FormNotFoundException;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.GigWorkerRequest;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.GigwWorkerResponse;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.UserRequestDto;
import et.com.gebeya.safaricom.coreservice.event.ClientCreatedEvent;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.enums.Authority;
import et.com.gebeya.safaricom.coreservice.repository.FormRepository;
import et.com.gebeya.safaricom.coreservice.repository.GigWorkerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GigWorkerService {
    private final GigWorkerRepository gigWorkerRepository;
    private final WebClient.Builder webClientBuilder;
    private final FormRepository formRepository;
    private final KafkaTemplate<String,ClientCreatedEvent> kafkaTemplate;

    @Transactional
    public String createGigWorkers(GigWorkerRequest gigWorkerRequest){
        GigWorker gigWorker=new GigWorker(gigWorkerRequest);
        createGigWorkersUserInformation(gigWorker);
        gigWorkerRepository.save(gigWorker);
        log.info("Gig-Worker {} is Created and saved",gigWorkerRequest.getFirstName());
        String fullName = gigWorker.getFirstName() + " " + gigWorker.getLastName();

        //kafkaTemplate.send("notificationTopic",new ClientCreatedEvent(gigWorker.getEmail(),fullName));
        return "Gig worker Signed up Successfully ";
    }
    private void createGigWorkersUserInformation(GigWorker gigWorker) {
        UserRequestDto newUser=UserRequestDto.builder()
                .userId(gigWorker.getId())
                .userName(gigWorker.getEmail())
                .name(gigWorker.getFirstName())
                .password(gigWorker.getPassword())
                .authority(Authority.GIGWORKER)
                .isActive(true)
                .build();

        String response = webClientBuilder.build().post()
                .uri("http://identity-service/api/auth/register")
                .bodyValue(newUser)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("Response from identity micro service==> {}", response);
    }


    public List<GigwWorkerResponse> getAllGigWorker(){
        List<GigWorker> gigWorkers= gigWorkerRepository.findAll();
        return gigWorkers.stream().map(this::mapToClientResponse).toList();
    }

    private GigwWorkerResponse mapToClientResponse(GigWorker gigWorker) {
        return new GigwWorkerResponse(gigWorker);
    }


    public GigwWorkerResponse getGigWorkerById(Long gigWorkerId) {
        return new GigwWorkerResponse(gigWorkerRepository.findById(gigWorkerId)
                 .orElseThrow(() -> new RuntimeException("GigWorker not found with id: " + gigWorkerId)));
    }
    public GigWorker getGigWorkerByIdg(Long gigWorkerId) {

        return gigWorkerRepository.findById(gigWorkerId)
                .orElseThrow(() -> new RuntimeException("GigWorker not found with id: " + gigWorkerId));
    }
    public GigWorker assignJobToGigWorker(Long gigWorkerId, Long formId) {
        // Validate gig worker ID
        GigWorker gigWorker = gigWorkerRepository.findById(gigWorkerId)
                .orElseThrow(() -> new et.com.gebeya.safaricom.sebsabi.Exceptions.GigWorkerNotFoundException("Gig worker not found with ID: " + gigWorkerId));

        // Validate form ID
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new FormNotFoundException("Form not found with ID: " + formId));

        // Assign the job to the gig worker by updating the entity
        gigWorker.setAssignedForm(form);

        // Save the updated gig worker entity
        return gigWorkerRepository.save(gigWorker);
    }
}
