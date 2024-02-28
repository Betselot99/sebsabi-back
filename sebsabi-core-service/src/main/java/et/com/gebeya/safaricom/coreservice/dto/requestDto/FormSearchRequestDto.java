package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import et.com.gebeya.safaricom.coreservice.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FormSearchRequestDto {

    private static final String CLIENT_ID_PARAM = "clientId";
    private static final String STATUS_PARAM = "status";

    private  Long clientId;
    private  Status status;

    public FormSearchRequestDto(Map<String, String> request) {
        if (request == null) {
            this.clientId = null;
            this.status = null;
            return;
        }
        this.clientId = parseLongParameter(request.get(CLIENT_ID_PARAM));
        this.status = parseStatusParameter(request.get(STATUS_PARAM));
    }

    private Long parseLongParameter(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Status parseStatusParameter(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Status.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
