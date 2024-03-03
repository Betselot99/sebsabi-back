package et.com.gebeya.safaricom.coreservice.repository;

import et.com.gebeya.safaricom.coreservice.model.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserResponseRepository extends JpaRepository<UserResponse,Long> {
    @Query("SELECT COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId")
    long countUserResponsesByFormId(Long formId);
}
