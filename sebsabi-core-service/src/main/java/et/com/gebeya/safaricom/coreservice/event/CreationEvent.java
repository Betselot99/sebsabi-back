package et.com.gebeya.safaricom.coreservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreationEvent {
    private String email;
    private String name;
}

