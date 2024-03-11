package et.com.gebeya.safaricom.coreservice.repository.specification;

import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.Status;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class FormSpecifications {


    public static Specification<Form> formByTitle(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Form> formByCreatedOn(LocalDate createdOn) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdOn"), createdOn);
    }

    public static Specification<Form> formByClientId(Long clientId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("client").get("id"), clientId);
    }
}