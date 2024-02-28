package et.com.gebeya.identityservice.event;

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
