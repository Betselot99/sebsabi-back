package et.com.gebeya.safaricom.coreservice.service;


import et.com.gebeya.safaricom.coreservice.dto.analysisDto.*;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormQuestionDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormSearchRequestDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.FormGigworkerDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.JobFormDisplaydto;
import et.com.gebeya.safaricom.coreservice.model.*;
import et.com.gebeya.safaricom.coreservice.model.enums.QuestionType;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import et.com.gebeya.safaricom.coreservice.repository.FormRepository;
import et.com.gebeya.safaricom.coreservice.repository.specification.FormSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FormService {
    private final FormRepository formRepository;
    private final ClientService clientService;

    @Transactional
    public JobFormDisplaydto createForm(FormDto formDto, Long clientId) {
        formDto.getStatus();

        Form newForm = new Form(formDto);
        Optional<Client> clientInfo = clientService.getClientId(clientId);
        if (clientInfo.isPresent()) {
            Client newClient = clientInfo.get();
            newForm.setClient(newClient);
        }
        log.info("New form created titled: {}", newForm.getTitle());
        return new JobFormDisplaydto(formRepository.save(newForm));
    }


    public Form addQuestionsToForm(Long formId, List<FormQuestionDto> questionDTOs) {
        Form form = getFormById(formId);

        for (FormQuestionDto questionDTO : questionDTOs) {
            FormQuestion question = new FormQuestion(questionDTO);
            question.setForm(form);

            if (questionDTO.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                List<MultipleChoiceOption> options = questionDTO.getMultipleChoiceOptions().stream()
                        .map(optionText -> {
                            MultipleChoiceOption option = new MultipleChoiceOption();
                            option.setOptionText(String.valueOf(optionText));
                            option.setFormQuestion(question);
                            return option;
                        })
                        .collect(Collectors.toList());
                question.setMultipleChoiceOptions(options);
            } else if (questionDTO.getQuestionType() == QuestionType.RATING_SCALE) {
                question.setRatingScale(questionDTO.getRatingScale());
            }

            form.getQuestions().add(question);
        }

        return formRepository.save(form);
    }

    public List<Form> getAllForms() {
        return formRepository.findAll();
    }


    public Form getFormById(Long id) {
        return formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with id: " + id));
    }

    public Optional<Form> getFormByClientId(Long clientId) {
        return formRepository.findFormByClient_Id(clientId);
    }


    public List<Form> getFormsByStatus(Status status) {
        return formRepository.findFormsByStatus(status);
    }

//    public List<Form> getFormsByStatusClamied(Long gigWorkerId,Status status) {
//        return formRepository.findFormsByAssignedGigWorkerIdAndStatus(gigWorkerId,Status.Claimed);
//    }
//


    public List<Form> getFormsByClientIdAndStatus(Long client_id, Status status) {
        return formRepository.findFormsByClient_IdAndStatus(client_id, status);
    }
    public List<Form> getFormsByGigWorkerIdAndStatus(Long gig_worker_id, Status status) {
        return formRepository.findFormsByAssignedGigWorkerIdAndStatus(gig_worker_id, status);
    }


//    public ResponseEntity<List<Form>> getForms(FormSearchRequestDto formSearch, Pageable pageable) {
//        Long clientId = formSearch.getClientId();
//        Status status = formSearch.getStatus();
//
//        Page<Form> formPage;
//
//        if (clientId != null && status != null) {
//            // Both client ID and status are provided
//            formPage = formRepository.findAll(FormSpecifications.formByClientIdAndStatus(clientId, status), pageable);
//        } else if (clientId != null) {
//            // Only client ID is provided
//            formPage = formRepository.findAll(FormSpecifications.formByClientId(clientId), pageable);
//        } else if (status != null) {
//            // Only status is provided
//            formPage = formRepository.findAll(FormSpecifications.formByStatus(status), pageable);
//        } else {
//            // Neither client ID nor status is provided
//            formPage = formRepository.findAll(pageable);
//        }
//
//        List<Form> forms = formPage.getContent();
//        if (forms.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(forms);
//        } else {
//            return ResponseEntity.ok(forms);
//        }
//    }


    public Form updateForm(Long id, FormDto formDTO) throws InvocationTargetException, IllegalAccessException {
        Form existingForm = getFormById(id);
        BeanUtilsBean notNullBeanUtils = new NullAwareBeanUtilsBean(); // Use a custom BeanUtilsBean to handle null properties
        notNullBeanUtils.copyProperties(existingForm, formDTO);
        return formRepository.save(existingForm);
    }


    public static class NullAwareBeanUtilsBean extends BeanUtilsBean {
        @Override
        public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
            if (value != null) {
                if (value instanceof Integer && (Integer) value == 0) {
                    // If the value is 0 (default value for int), we don't want to copy it
                    return;
                }
                super.copyProperty(dest, name, value);
            }
        }
    }

    public Form getFormForGigWorker(Long formId, Long gig_worker_id) throws AccessDeniedException {
        return formRepository.findFormByIdAndAssignedGigWorkerId(formId, gig_worker_id).orElseThrow(() -> new AccessDeniedException("You are not authorized to access this form"));
    }
    public Form getFormForGigWorkerAndClient(Long form_id,Long client_id, Long gig_worker_id) throws AccessDeniedException {
        return formRepository.findFormsByIdAndClient_IdAndAssignedGigWorkerId(form_id,client_id, gig_worker_id);
    }
    public Form getFormForClientByFormId(Long formId, Long clientId) throws AccessDeniedException {
        return formRepository.findFormByIdAndClient_Id(formId, clientId).orElseThrow(() -> new AccessDeniedException("You are not authorized to access this form"));
    }

//    Object Mapper

    public List<FormsByStatusDTO> countFormsByStatus() {
        return formRepository.countFormsByStatus()
                .stream()
                .map(obj -> new FormsByStatusDTO((Status) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }

    public List<FormsPerClientDTO> countFormsPerClient() {
        return formRepository.countFormsPerClient()
                .stream()
                .map(obj -> new FormsPerClientDTO((Long) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }

    public List<FormsAssignedToGigWorkersDTO> countFormsAssignedToGigWorkers() {
        return formRepository.countFormsAssignedToGigWorkers()
                .stream()
                .map(obj -> new FormsAssignedToGigWorkersDTO((Long) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }

    public List<ProposalsPerFormDTO> countProposalsPerForm() {
        return formRepository.countProposalsPerForm()
                .stream()
                .map(obj -> new ProposalsPerFormDTO((Long) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }

    public List<FormsPerClientByStatusDTO> countFormsPerClientByStatus(Status status) {
        return formRepository.countFormsPerClientByStatus(status)
                .stream()
                .map(obj -> new FormsPerClientByStatusDTO((Long) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }
    public Page<Form> searchForms(FormSearchRequestDto searchRequestDto, Pageable pageable) {
        Specification<Form> spec = Specification.where(null);

        String title = searchRequestDto.getTitle();
        if (title != null && !title.isEmpty()) {
            spec = spec.and(FormSpecifications.formByTitle(title));
        }

        LocalDate createdOn = searchRequestDto.getCreatedOn();
        if (createdOn != null) {
            spec = spec.and(FormSpecifications.formByCreatedOn(createdOn));
        }

        return formRepository.findAll(spec, pageable);
    }
    public Page<Form> searchClientForms(FormSearchRequestDto searchRequestDto,Long clientId, Pageable pageable) {

        Specification<Form> spec = Specification.where(null);

        String title = searchRequestDto.getTitle();
        if (title != null && !title.isEmpty()) {
            spec = spec.and(FormSpecifications.formByTitle(title));
        }

        LocalDate createdOn = searchRequestDto.getCreatedOn();
        if (createdOn != null) {
            spec = spec.and(FormSpecifications.formByCreatedOn(createdOn));
        }

        // Add specification to filter by client ID
        spec = spec.and(FormSpecifications.formByClientId(clientId));

        return formRepository.findAll(spec, pageable);
    }
}
