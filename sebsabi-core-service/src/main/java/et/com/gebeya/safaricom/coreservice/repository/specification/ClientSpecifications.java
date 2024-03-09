package et.com.gebeya.safaricom.coreservice.repository.specification;


import et.com.gebeya.safaricom.coreservice.model.Client;
import org.springframework.data.jpa.domain.Specification;

public class ClientSpecifications {

    public static Specification<Client> clientByFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Client> clientByLastName(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Client> clientByCompanyType(String companyType) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("companyType")), "%" + companyType.toLowerCase() + "%");
    }

    public static Specification<Client> clientByEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
    public static Specification<Client> clientByIsActive(boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }
}