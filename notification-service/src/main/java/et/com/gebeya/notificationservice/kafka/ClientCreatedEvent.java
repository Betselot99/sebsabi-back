package et.com.gebeya.notificationservice.kafka;

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
