package et.com.gebeya.safaricom.coreservice.repository.specification;


import et.com.gebeya.safaricom.coreservice.model.Client;
import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import org.springframework.data.jpa.domain.Specification;

public class GigworkerSpecifications {

    public static Specification<GigWorker> gigWorkerByFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<GigWorker> gigWorkerByLastName(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<GigWorker> gigWorkerByQualification(String qualification) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("qualification")), "%" + qualification.toLowerCase() + "%");
    }

    public static Specification<GigWorker> gigWorkerByEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
    public static Specification<GigWorker> gigWorkerByIsActive(boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }
}