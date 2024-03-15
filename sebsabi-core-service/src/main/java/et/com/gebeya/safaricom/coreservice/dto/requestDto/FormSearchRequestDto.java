package et.com.gebeya.safaricom.coreservice.dto.requestDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
public class FormSearchRequestDto {

    private String title;
    private LocalDate createdOn;

    public FormSearchRequestDto(Map<String, String> requestParams) {
        this.title = requestParams.getOrDefault("title", "");
        // Parse createdOn date if provided in the request parameters
        String createdOnString = requestParams.get("createdOn");
        if (createdOnString != null && !createdOnString.isEmpty()) {
            this.createdOn = LocalDate.parse(createdOnString);
        }
    }
}