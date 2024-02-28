package et.com.gebeya.safaricom.coreservice.model;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.ClientRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "clients")
public class Client extends Person{

   private String companyName;
   private String companyType;
   private String occupation;

   @OneToMany(cascade = CascadeType.ALL)
   @JoinColumn(name = "form_id")
   private List<Form> forms;

   public Client(ClientRequest clientRequest){
      this.setFirstName(clientRequest.getFirstName());
      this.setLastName(clientRequest.getLastName());
      this.setEmail(clientRequest.getEmail());
      this.setPassword(clientRequest.getPassword());
      this.setOccupation(clientRequest.getOccupation());
      this.setCompanyName(clientRequest.getCompanyName());
      this.setCompanyType(clientRequest.getCompanyType());
      this.setIsActive(clientRequest.getIsActive());
   }
}
