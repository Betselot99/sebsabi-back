package et.com.gebeya.safaricom.coreservice.dto.responseDto;

import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.FormQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobFormDisplaydto {
    private Long id;
    private String title;
    private String description;
    private int usageLimit;
    private List<FormQuestion> formQuestionList;

    public JobFormDisplaydto(Form form){
        this.id=form.getId();
        this.title=form.getTitle();
        this.description=form.getDescription();
        this.usageLimit=form.getUsageLimit();
    }

}
