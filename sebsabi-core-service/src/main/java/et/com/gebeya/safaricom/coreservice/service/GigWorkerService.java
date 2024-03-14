package et.com.gebeya.safaricom.coreservice.service;


import et.com.gebeya.safaricom.coreservice.Exceptions.FormNotFoundException;
import et.com.gebeya.safaricom.coreservice.Exceptions.GigWorkerNotFoundException;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.GigWorkerRequest;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.GigWorkerSearchRequestDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.GigwWorkerResponse;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.UserRequestDto;
import et.com.gebeya.safaricom.coreservice.event.ClientCreatedEvent;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import et.com.gebeya.safaricom.coreservice.model.enums.Authority;
import et.com.gebeya.safaricom.coreservice.repository.FormRepository;
import et.com.gebeya.safaricom.coreservice.repository.GigWorkerRepository;

import et.com.gebeya.safaricom.coreservice.repository.specification.GigworkerSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

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
        gigWorkerRepository.save(gigWorker);
        createGigWorkersUserInformation(gigWorker);
        log.info("Gig-Worker {} is Created and saved",gigWorkerRequest.getFirstName());
        String fullName = gigWorker.getFirstName() + " " + gigWorker.getLastName();

        kafkaTemplate.send("notificationTopic",new ClientCreatedEvent(gigWorker.getEmail(),fullName));
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
                .orElseThrow(() ->new GigWorkerNotFoundException("Gig worker not found with ID: " + gigWorkerId));

        // Validate form ID
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new FormNotFoundException("Form not found with ID: " + formId));

        // Assign the job to the gig worker by updating the entity
        gigWorker.setAssignedForm(form);

        // Save the updated gig worker entity
        return gigWorkerRepository.save(gigWorker);
    }

    public void updateGigworkersUserInformation(GigWorker gigWorker,String newPassword) {
        // Create a UserRequestDto object with only the password field set
        // Create a UserRequestDto object with only the password field set
        UserRequestDto updateUser = UserRequestDto.builder()
                .userId(gigWorker.getId())
                .userName(gigWorker.getEmail())
                .name(gigWorker.getFirstName())
                .password(newPassword)
                .authority(Authority.CLIENT)
                .isActive(true)
                .build(); // Build the UserRequestDto object
        log.info(updateUser.toString());

        // Make a POST request to update the password in the identity service
        String response = webClientBuilder.build().post()
                .uri("http://identity-service/api/auth/reset/password")
                .bodyValue(updateUser) // Pass the UserRequestDto object as the body
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Log the response from the identity microservice
        // log.info("Response from identity micro service==> {}", response);
    }




    public GigwWorkerResponse updateGigworker(Long id, GigWorkerRequest gigWorkerRequest) throws InvocationTargetException, IllegalAccessException {
        Optional<GigWorker> existingGigworkerOptional = gigWorkerRepository.findById(id);
        if (existingGigworkerOptional.isPresent()) {
            GigWorker existingGigworker = existingGigworkerOptional.get();

            // Check if the ClientRequest contains a non-null password
            if (gigWorkerRequest.getPassword() != null && !gigWorkerRequest.getPassword().isEmpty()) {
                // If password is being updated, call updateClientsUserInformation method


                updateGigworkersUserInformation(existingGigworker,gigWorkerRequest.getPassword());
                existingGigworker.setPassword(passwordEncoder().encode(gigWorkerRequest.getPassword()));
            }

            // Use NullAwareBeanUtilsBean to handle null properties
            BeanUtilsBean notNullBeanUtils = new NullAwareBeanUtilsBean();
            notNullBeanUtils.copyProperties(existingGigworker, gigWorkerRequest);

            // Save the updated client
            GigWorker updatedGigworker = gigWorkerRepository.save(existingGigworker);

            // Return the updated client as ClientResponse
            return new GigwWorkerResponse(updatedGigworker);
        } else {
            throw new RuntimeException("Client not found with id: " + id);
        }
    }
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public static class NullAwareBeanUtilsBean extends BeanUtilsBean {
        @Override
        public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
            if (value != null) {
                if (value instanceof Integer && (Integer) value == 0) {
                    // If the value is 0 (default value for int), we don't want to copy it
                    return;
                }
                if (value instanceof Byte && (Byte) value == 0) {
                    // If the value is 0 (default value for int), we don't want to copy it
                    return;
                }
                if (value instanceof Enum && ((Enum<?>) value).ordinal() == 0) {
                    // If the value is the first enum constant (ordinal 0), we don't want to copy it
                    return;
                }
                if (dest instanceof Status && value instanceof String) {
                    // If the destination is of type Status enum and value is a string, convert it to Status enum
                    Status status = Status.valueOf(((String) value).toUpperCase()); // Convert the string to uppercase before converting to enum
                    super.copyProperty(dest, name, status);
                    return;
                }
                super.copyProperty(dest, name, value);
            }
        }
    }
    public long getNumberofGigWokers(){
        return gigWorkerRepository.countGigWorkersByIsActive(Status.Active);
    }
    public Page<GigWorker> searchGigworker(GigWorkerSearchRequestDto searchRequestDto, Pageable pageable) {
        String firstName = searchRequestDto.getFirstName();
        String lastName = searchRequestDto.getLastName();
        String qualification = searchRequestDto.getQualification();
        String email = searchRequestDto.getEmail();


        Specification<GigWorker> spec = Specification.where(null);

        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and(GigworkerSpecifications.gigWorkerByFirstName(firstName));
        }

        if (lastName != null && !lastName.isEmpty()) {
            spec = spec.and(GigworkerSpecifications.gigWorkerByLastName(lastName));
        }

        if (qualification != null && !qualification.isEmpty()) {
            spec = spec.and(GigworkerSpecifications.gigWorkerByQualification(qualification));
        }

        if (email != null && !email.isEmpty()) {
            spec = spec.and(GigworkerSpecifications.gigWorkerByEmail(email));
        }
        // Adding isActive criteria



        return gigWorkerRepository.findAll(spec, pageable);
    }
}
