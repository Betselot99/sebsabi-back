package et.com.gebeya.safaricom.coreservice.dto.responseDto;

import et.com.gebeya.safaricom.coreservice.model.enums.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserInformation {
    private String name;
    private String userName;
    private String password;
    private Authority authority;
    private Long userId;
    private Boolean isActive;
}
