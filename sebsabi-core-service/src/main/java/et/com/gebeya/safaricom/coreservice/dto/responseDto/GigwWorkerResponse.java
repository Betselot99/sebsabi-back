package et.com.gebeya.safaricom.coreservice.dto.responseDto;

import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.Testimonial;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

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
    private String password;
    private Status isActive;
    private List<Testimonial> testimonials;

    public GigwWorkerResponse(GigWorker gigWorker){
      //  this.id=gigWorker.getId();
        this.firstName = gigWorker.getFirstName();
        this.lastName= gigWorker.getLastName();
        this.qualification= gigWorker.getQualification();
        this.password= gigWorker.getPassword();
        this.email=gigWorker.getEmail();
        this.dob=gigWorker.getDob();
        this.isActive=gigWorker.getIsActive();

        this.age=gigWorker.getAge();
    }
}
