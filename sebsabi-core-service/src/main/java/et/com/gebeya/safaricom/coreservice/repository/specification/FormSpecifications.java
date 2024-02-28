package et.com.gebeya.safaricom.coreservice.repository.specification;

import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.Status;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
public class FormSpecifications {

    public static Specification<Form> formByClientId(Long clientId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("client").get("id"), clientId);
    }

    public static Specification<Form> formByStatus(Status status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Form> formByClientIdAndStatus(Long clientId, Status status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("client").get("id"), clientId));
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}