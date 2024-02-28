package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.model.Answer;
import et.com.gebeya.safaricom.coreservice.model.UserResponse;
import et.com.gebeya.safaricom.coreservice.repository.UserResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserResponseService {


    private final UserResponseRepository userResponseRepository;

    private final AnswerService answerService;
    public UserResponse saveResponse(UserResponse userResponse) {
        UserResponse savedResponse = userResponseRepository.save(userResponse);

        // Save answers
        List<Answer> answers = userResponse.getAnswers();
        if (answers != null) {
            answers.forEach(answer -> {
                answer.setUserResponse(savedResponse);
                answerService.saveAnswer(answer);
            });
        }

        return savedResponse;
    }
}
