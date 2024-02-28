package et.com.gebeya.identityservice.dto.requestDto;

import et.com.gebeya.identityservice.entity.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {
    private String name;
    private String userName;
    private String password;
    private Authority authority;
    private Long userId;
    private Boolean isActive;
}
