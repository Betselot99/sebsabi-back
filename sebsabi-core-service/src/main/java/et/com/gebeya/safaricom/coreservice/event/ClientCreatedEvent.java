package et.com.gebeya.safaricom.coreservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientCreatedEvent {
    private String clientEmail;
    private String clientName;

}
