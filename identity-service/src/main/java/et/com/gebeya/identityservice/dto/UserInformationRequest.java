package et.com.gebeya.identityservice.dto;

import et.com.gebeya.identityservice.entity.Authority;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInformationRequest {
    private String name;
    private String userName;
    private String password;
    private Authority authority;
    private Long userId;
    private Boolean isActive;
}
