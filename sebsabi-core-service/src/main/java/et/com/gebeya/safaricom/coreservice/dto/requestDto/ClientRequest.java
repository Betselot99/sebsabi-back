package et.com.gebeya.safaricom.coreservice.dto.requestDto;

import et.com.gebeya.safaricom.coreservice.model.Status;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequest {
    @NotBlank(message = "Firstname is required")
    private String firstName;
    @NotBlank(message = "Lastname is required")
    private String lastName;
    @Column(unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    private String password;
    private String companyName;
    private String companyType;
    private String occupation;
    private Status isActive;
    private byte[] profileImage;
}
