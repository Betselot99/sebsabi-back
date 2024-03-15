package et.com.gebeya.safaricom.coreservice.dto.responseDto;

import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.FormQuestion;
import et.com.gebeya.safaricom.coreservice.model.Proposal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormGigworkerDto {
    private Long id;
    private String title;
    private String description;
    private int usageLimit;
    private List<Proposal> proposals;

    public FormGigworkerDto(Form form){
        this.id=form.getId();
        this.title=form.getTitle();
        this.description=form.getDescription();
        this.usageLimit=form.getUsageLimit();
    }
}
