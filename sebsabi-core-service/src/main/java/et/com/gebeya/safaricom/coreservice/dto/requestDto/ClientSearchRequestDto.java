package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientSearchRequestDto {

    private String firstName;
    private String lastName;
    private String companyType;
    private String email;
    private Boolean isActive;


        public ClientSearchRequestDto(Map<String, String> requestParams) {
            this.firstName = requestParams.getOrDefault("firstName", "");
            this.lastName = requestParams.getOrDefault("lastName", "");
            this.companyType = requestParams.getOrDefault("companyType", "");
            this.email = requestParams.getOrDefault("email", "");
            String isActiveParam = requestParams.getOrDefault("isActive", "");
            this.isActive = isActiveParam.equalsIgnoreCase("true") || isActiveParam.equalsIgnoreCase("1");        }
}