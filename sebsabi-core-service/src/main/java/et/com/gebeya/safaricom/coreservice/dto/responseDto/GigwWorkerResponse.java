package et.com.gebeya.safaricom.coreservice.dto.responseDto;

import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GigwWorkerResponse {
    //private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String qualification;
    private Date dob;
    private int age;
    private Status isActive;

    public GigwWorkerResponse(GigWorker gigWorker){
      //  this.id=gigWorker.getId();
        this.firstName = gigWorker.getFirstName();
        this.lastName= gigWorker.getLastName();
        this.qualification= gigWorker.getQualification();
        this.email=gigWorker.getEmail();
        this.dob=gigWorker.getDob();
        this.age=gigWorker.getAge();
    }
}
