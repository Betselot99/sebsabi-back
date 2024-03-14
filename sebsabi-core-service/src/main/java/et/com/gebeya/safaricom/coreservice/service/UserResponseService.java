package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.AnswerDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.UserResponseRequestDto;
import et.com.gebeya.safaricom.coreservice.model.*;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import et.com.gebeya.safaricom.coreservice.repository.FormRepository;
import et.com.gebeya.safaricom.coreservice.repository.UserResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserResponseService {


    private final UserResponseRepository userResponseRepository;
    private final FormService formService;
    private final FormRepository formRepository;
    private final GigWorkerService gigWorkerService;

    private final AnswerService answerService;
    public UserResponse submitResponse(UserResponseRequestDto responseDTO) throws AccessDeniedException {
        // Retrieve the form for the gig worker
        Form form = formService.getFormForGigWorker(responseDTO.getFormId(), responseDTO.getGigWorkerId());
        Boolean formLimitReached = getCountOfResponsesByFormIdAndUsageLimit(form);

        if (!formLimitReached) {
            // Retrieve the gig worker by ID
            GigWorker gigWorker = gigWorkerService.getGigWorkerByIdg(responseDTO.getGigWorkerId());

            // Create a new UserResponse entity
            UserResponse userResponse = new UserResponse();
            userResponse.setForm(form);
            userResponse.setGigWorker(gigWorker);

            // Create Answer entities for each question
            List<Answer> answers = new ArrayList<>();
            for (AnswerDto answerDto : responseDTO.getAnswers()) {
                FormQuestion question = form.getQuestions().stream()
                        .filter(q -> q.getId().equals(answerDto.getQuestionId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Question not found in the form"));

                Answer answer = new Answer();
                answer.setQuestion(question);

                // Handle different question types
                switch (question.getQuestionType()) {
                    case TEXT -> {
                        answer.setAnswerText(answerDto.getAnswerText());
                    }
                    case TRUE_FALSE -> {
                        answer.setAnswerText(answerDto.getAnswerText());
                        break;
                    }
                    case MULTIPLE_CHOICE -> {
                        List<MultipleChoiceOption> selectedOptions = answerDto.getSelectedOptions().stream()
                                .map(optionId -> question.getMultipleChoiceOptions().stream()
                                        .filter(option -> option.getId().equals(optionId))
                                        .findFirst()
                                        .orElseThrow(() -> new RuntimeException("Option not found for multiple choice question")))
                                .collect(Collectors.toList());
                        answer.setSelectedOptions(selectedOptions);
                    }
                    case RATING_SCALE -> {
                        answer.setRating(answerDto.getRating());
                    }
                    default -> throw new RuntimeException("Unsupported question type");
                }

                answer.setUserResponse(userResponse);
                answers.add(answer);
            }

            // Set the answers in the UserResponse entity
            userResponse.setAnswers(answers);

            // Save the UserResponse entity
            if (form.getUsageLimit() - countUserResponsesByFormId(form.getId()) == 1) {
                form.setStatus(Status.Completed);
            }
            formRepository.save(form);
            userResponse=userResponseRepository.save(userResponse);
            return userResponse;
        } else {
            form.setStatus(Status.Completed);
            formRepository.save(form);
            throw new RuntimeException("Form limit has been reached");
        }

    }



    private Boolean getCountOfResponsesByFormIdAndUsageLimit(Form form){
        int usageLimit=form.getUsageLimit();
        log.info(String.valueOf(usageLimit));

        long submitCountForForm=countUserResponsesByFormId(form.getId());
        log.info(String.valueOf(submitCountForForm));
        return submitCountForForm >= usageLimit;
    }
    public Long jobStatusForClient(long formId, long clientId) throws AccessDeniedException {
        Form formByClientId = formService.getFormForClientByFormId(formId, clientId);
        if (formByClientId != null && formByClientId.getId() == formId) {
            return calculateJobStatus(formId);
        } else {
            throw new AccessDeniedException("Client is not authorized to access this form");
        }
    }


    public Long jobStatusForGigWorker(long formId, long gigWorkerId) throws AccessDeniedException {
        Form formForGigWorker = formService.getFormForGigWorker(formId, gigWorkerId);
        if (formForGigWorker != null) {
            return calculateJobStatus(formId);
        } else {
            throw new AccessDeniedException("Gig worker is not authorized to access this form");
        }
    }

    private Long calculateJobStatus(long formId) {
        Form form = formService.getFormById(formId);
        int usageLimit = form.getUsageLimit();
        long submitCountForForm = countUserResponsesByFormId(formId);

        // Calculate progress as a percentage
        if (usageLimit > 0) {
            return (submitCountForForm * 100) / usageLimit;
        } else {
            return 0L; // Return 0 if usage limit is not set to avoid division by zero
        }
    }

    public long countUserResponsesByFormId(Long formId) {
        return userResponseRepository.countUserResponsesByFormId(formId);
    }
}
