package et.com.gebeya.safaricom.coreservice.model;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.GigWorkerRequest;
import jakarta.persistence.*;
import jakarta.validation.Valid;
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
@Entity
@Table(name = "gigworkers")
public class GigWorker extends Person{
    private String qualification;
    private int Age;
    private Date dob;
    @OneToOne
    @JoinColumn(name = "assigned_form_id") // This assumes the column name in your GigWorker table
    @Valid
    private Form assignedForm;
    // One-to-many relationship with Testimonials
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gigWorker")
    private List<Testimonial> testimonials;


    // One-to-many relationship with Ratings
    @OneToMany
    private List<Rating> ratings;
    public GigWorker(GigWorkerRequest gigWorkerRequest){
        this.setFirstName(gigWorkerRequest.getFirstName());
        this.setLastName(gigWorkerRequest.getLastName());
        this.setEmail(gigWorkerRequest.getEmail());
        this.setPassword(gigWorkerRequest.getPassword());
        this.setQualification(gigWorkerRequest.getQualification());
        this.setDob(gigWorkerRequest.getDob());
        this.setAge(gigWorkerRequest.getAge());
        this.setIsActive(gigWorkerRequest.getIsActive());
    }

}
