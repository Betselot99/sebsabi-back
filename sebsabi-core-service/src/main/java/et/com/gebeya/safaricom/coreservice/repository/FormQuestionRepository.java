package et.com.gebeya.safaricom.coreservice.repository;

import et.com.gebeya.safaricom.coreservice.model.FormQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormQuestionRepository extends JpaRepository<FormQuestion,Long> {

    List<FormQuestion> getFormQuestionByForm_Id(Long form_id);
}
