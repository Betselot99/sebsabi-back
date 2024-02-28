package et.com.gebeya.notificationservice.kafka;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PasswordResetEvent {
    private String email;
    private String resetToken;

}
