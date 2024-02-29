package et.com.gebeya.identityservice.service;

import et.com.gebeya.identityservice.entity.Authority;
import et.com.gebeya.identityservice.event.PasswordResetEvent;
import et.com.gebeya.identityservice.dto.requestDto.UserRequestDto;
import et.com.gebeya.identityservice.dto.responseDto.UserResponseDto;
import et.com.gebeya.identityservice.entity.UserCredentials;
import et.com.gebeya.identityservice.repository.UserCredentialsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCredentialsService {
    private final UserCredentialsRepository userCredentialsRepository;
    private final KafkaTemplate<String, PasswordResetEvent> kafkaTemplate;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userCredentialsRepository.findFirstByUserName(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    public UserCredentials createUpdateUser(UserCredentials users) {
        return userCredentialsRepository.save(users);
    }

    public UserCredentials loadUserByUsername(String userName) {
        return userCredentialsRepository.findFirstByUserName(userName).orElseThrow(() -> new IllegalArgumentException("Invalid user name or password"));
    }

    public Optional<UserCredentials> getUsers(String userName){
        return userCredentialsRepository.findFirstByUserName(userName);
    }
    public UserResponseDto signUp(UserRequestDto userRequestDto){
        UserCredentials newUser=UserCredentials.builder()
                .name(userRequestDto.getName())
                .userName(userRequestDto.getUserName())
                .isActive(userRequestDto.getIsActive())
                .userId(userRequestDto.getUserId())
                .authority(userRequestDto.getAuthority())
                .password(passwordEncoder().encode(userRequestDto.getPassword()))
                .build();

        userCredentialsRepository.save(newUser);

        return new UserResponseDto(newUser);
    }

    public UserResponseDto updateUsers(UserRequestDto userRequestDto) throws InvocationTargetException, IllegalAccessException {
        Optional<UserCredentials> existingUserOptional = userCredentialsRepository.findByUserId(userRequestDto.getUserId());
        if (existingUserOptional.isPresent()) {
            UserCredentials existingClient = existingUserOptional.get();

            // Use NullAwareBeanUtilsBean to handle null properties
            BeanUtilsBean notNullBeanUtils = new NullAwareBeanUtilsBean();
            notNullBeanUtils.copyProperties(existingClient, userRequestDto.getPassword());

            // Save the updated client
            UserCredentials updatedUser = userCredentialsRepository.save(existingClient);

            // Return the updated client as ClientResponse

            return new UserResponseDto(updatedUser);
        } else {
            throw new RuntimeException("User not found with id: " + userRequestDto.getUserName());
        }
    }

    public static class NullAwareBeanUtilsBean extends BeanUtilsBean {
        @Override
        public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
            if (value != null) {
                if (value instanceof Integer && (Integer) value == 0) {
                    // If the value is 0 (default value for int), we don't want to copy it
                    return;
                }
                if (value instanceof Boolean) {
                    // If the value is of type Boolean, directly copy it
                    super.copyProperty(dest, name, value);
                    return;
                }
                if (dest instanceof Authority && value instanceof String) {
                    // If the destination is of type Status enum and value is a string, convert it to Status enum
                    Authority status = Authority.valueOf(((String) value).toUpperCase()); // Convert the string to uppercase before converting to enum
                    super.copyProperty(dest, name, status);
                    return;
                }
                super.copyProperty(dest, name, value);
            }
        }
    }

    public Boolean requestPasswordReset(String username) {
        Optional<UserCredentials> optionalUser = userCredentialsRepository.findFirstByUserName(username);
        if (optionalUser.isPresent()) {
            UserCredentials user = optionalUser.get();
            String resetToken = generateResetToken(user.getUserId());
            log.info(resetToken);
            user.setResetToken(resetToken);
            userCredentialsRepository.save(user);
            // Trigger Kafka event for password reset
            kafkaTemplate.send("PasswordResetEvent", new PasswordResetEvent(user.getUsername(), resetToken));
            return true;
        } else {
            throw new RuntimeException("User not found with username: " + username);
        }
    }

    private String generateResetToken(Long userId) {
        // Generate a unique UUID for the token
        String token = UUID.randomUUID().toString();
        // Combine the user ID and token with a hyphen separator
        return userId + "-" + token;

    }

    public Boolean updatePassword(String resetToken, String newPassword) {
        // Extract user ID from the reset token
        Long userId = extractUserIdFromToken(resetToken);

        // Retrieve the user by ID
        Optional<UserCredentials> optionalUser = userCredentialsRepository.findByUserId(userId);
        if (optionalUser.isPresent()) {
            UserCredentials user = optionalUser.get();

            // Perform additional checks here, if needed

            // Update the user's password
            user.setPassword(passwordEncoder().encode(newPassword)); // Assuming you have a method to encode passwords
            user.setResetToken(null); // Clear the reset token after password update
            userCredentialsRepository.save(user);
            return true;// Save the updated user
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }


    private Long extractUserIdFromToken(String resetToken) {
        // Log the token to check its format
        System.out.println("Reset token: " + resetToken);

        // Define a regular expression pattern to match the user ID
        Pattern pattern = Pattern.compile("^([0-9]+)-.*$");
        Matcher matcher = pattern.matcher(resetToken);

        // Check if the reset token matches the pattern
        if (matcher.matches()) {
            // Extract the user ID from the first capturing group
            return Long.parseLong(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Invalid reset token format.");
        }

    }
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}