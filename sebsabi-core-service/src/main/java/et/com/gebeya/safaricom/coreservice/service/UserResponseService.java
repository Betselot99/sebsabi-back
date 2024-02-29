package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.UserResponseRequestDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.GigwWorkerResponse;
import et.com.gebeya.safaricom.coreservice.model.*;
import et.com.gebeya.safaricom.coreservice.repository.UserResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserResponseService {


    private final UserResponseRepository userResponseRepository;
    private final FormService formService;
    private final GigWorkerService gigWorkerService;

    private final AnswerService answerService;
    public UserResponse submitResponse(UserResponseRequestDto responseDTO) throws AccessDeniedException {
        // Retrieve the form for the gig worker
        Form form = formService.getFormForGigWorker(responseDTO.getFormId(), responseDTO.getGigWorkerId());

        // Retrieve the question by ID from the form
        FormQuestion question = form.getQuestions().stream()
                .filter(q -> q.getId().equals(responseDTO.getQuestionId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found in the form"));

        // Retrieve the gig worker by ID
        GigWorker gigWorker = gigWorkerService.getGigWorkerByIdg(responseDTO.getGigWorkerId());
        // Create a new UserResponse entity
        UserResponse userResponse = new UserResponse();
        userResponse.setForm(form);
        userResponse.setQuestion(question);
        userResponse.setGigWorker(gigWorker);

        // Create an Answer entity and set answerText
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setAnswerText(responseDTO.getUserAnswer());

        // Set the answer in the UserResponse entity
        userResponse.setAnswers(Collections.singletonList(answer));

        // Save the UserResponse entity
        return userResponseRepository.save(userResponse);
    }
}
