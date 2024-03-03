package et.com.gebeya.safaricom.coreservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne
 @JoinColumn(name = "form_id")
 @JsonIgnore
 private Form form;

 @ManyToOne
 @JoinColumn(name = "gig_worker_id")
 @JsonIgnore
 private GigWorker gigWorker;

 @OneToMany(mappedBy = "userResponse", cascade = CascadeType.ALL, orphanRemoval = true)
 private List<Answer> answers;
}

