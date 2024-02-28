package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.model.Answer;
import et.com.gebeya.safaricom.coreservice.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer saveAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

}
