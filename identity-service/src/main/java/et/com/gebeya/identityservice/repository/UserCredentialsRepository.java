package et.com.gebeya.identityservice.repository;

import et.com.gebeya.identityservice.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials,Long> {
    Optional<UserCredentials> findFirstByUserName(String userName);


    Optional<UserCredentials> findByUserId(Long userId);
}
