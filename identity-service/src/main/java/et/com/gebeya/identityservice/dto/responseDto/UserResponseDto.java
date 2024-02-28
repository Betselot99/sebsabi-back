package et.com.gebeya.identityservice.dto.responseDto;

import et.com.gebeya.identityservice.entity.Authority;
import et.com.gebeya.identityservice.entity.UserCredentials;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String name;
    private String userName;
    private String password;
    private Authority authority;
    private Long userId;
    private Boolean isActive;

    public UserResponseDto(UserCredentials userCredentials){
        this.userId=userCredentials.getUserId();
       // this.id=userCredentials.getId();
        this.userName=userCredentials.getUsername();
        this.isActive=userCredentials.isActive();
        //this.password=userCredentials.getPassword();
        this.authority=userCredentials.getAuthority();
    }
}
