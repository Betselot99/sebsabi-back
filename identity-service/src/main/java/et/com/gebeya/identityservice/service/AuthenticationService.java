package et.com.gebeya.identityservice.service;


import et.com.gebeya.identityservice.dto.UserInformationRequest;
import et.com.gebeya.identityservice.dto.requestDto.ResetPasswordRequestDto;
import et.com.gebeya.identityservice.dto.requestDto.TokenDto;
import et.com.gebeya.identityservice.dto.requestDto.UserInformation;
import et.com.gebeya.identityservice.dto.requestDto.UserRequestDto;
import et.com.gebeya.identityservice.dto.responseDto.AuthenticationResponse;
import et.com.gebeya.identityservice.dto.responseDto.ResetPasswordResponseDto;
import et.com.gebeya.identityservice.dto.responseDto.UserResponseDto;
import et.com.gebeya.identityservice.dto.responseDto.ValidationResponseDto;
import et.com.gebeya.identityservice.entity.UserCredentials;
import et.com.gebeya.identityservice.repository.UserCredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserCredentialsRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserCredentialsService userCredentialsService;
    public AuthenticationResponse signIn(UserInformation usersCredential)  {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usersCredential.getUsername(), usersCredential.getPassword()));
        UserCredentials user = userRepository.findFirstByUserName(usersCredential.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user name or password"));
        String jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwt).authority(user.getAuthority()).build();
    }
    public ResponseEntity<UserResponseDto> signup(UserRequestDto userRequestDto){
        final String userName=userRequestDto.getUserName();
            if(userRepository.findFirstByUserName(userName).isPresent()){
                throw new RuntimeException("User already exists with this email");
            }else{
                UserResponseDto responseDto=userCredentialsService.signUp(userRequestDto);
                new ResponseEntity<>(responseDto, HttpStatus.CREATED);
            }
        return new ResponseEntity<>(HttpStatus.IM_USED);
    }

    public ResponseEntity<ValidationResponseDto> validate(TokenDto token)
    {
        final String userName;
        userName = jwtService.extractUserName(token.getToken());
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        if (StringUtils.isNotEmpty(userName)) {
            UserCredentials users = userCredentialsService.loadUserByUsername(userName);
            if (jwtService.isTokenValid(token.getToken(), users)) {
                ValidationResponseDto response = ValidationResponseDto.builder()
                        .userId(users.getUserId())
                        .authority(users.getAuthorities().toString())
                        .build();

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<ResetPasswordResponseDto> requestPasswordChange(ResetPasswordRequestDto resetPasswordRequestDto) {
        if (StringUtils.isNotEmpty(resetPasswordRequestDto.getUsername())) {
            final String username= resetPasswordRequestDto.getUsername();
            UserCredentials users = userCredentialsService.loadUserByUsername(username);
                if(users!=null){
                  Boolean isReset=  userCredentialsService.requestPasswordReset(username);
                  if(isReset){

                     ResetPasswordResponseDto responseDto= ResetPasswordResponseDto.builder()
                              .message("Dear user an email has been sent please use that to reset the password ")
                             .build();
                     return new ResponseEntity<>(responseDto, HttpStatus.OK);
                  }
                }
            }
        ResetPasswordResponseDto responseDto= ResetPasswordResponseDto.builder()
                .message("User Not Found ")
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<ResetPasswordResponseDto> updatePassword(String resetToken, String newPassword) {
        if (StringUtils.isNotEmpty(resetToken)&& StringUtils.isNotEmpty(newPassword)) {
           Boolean isReset= userCredentialsService.updatePassword(resetToken,newPassword);
            if(isReset){

                ResetPasswordResponseDto responseDto= ResetPasswordResponseDto.builder()
                        .message("Password Retested Successfully please login with the new password ")
                        .build();
                return new ResponseEntity<>(responseDto, HttpStatus.OK);
            }
        }
        ResetPasswordResponseDto responseDto= ResetPasswordResponseDto.builder()
                .message("Reset Token is not Valid ")
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
    }
    public UserResponseDto updatePasswordNormally(UserRequestDto userRequestDto) throws InvocationTargetException, IllegalAccessException {
        if (userRequestDto!=null) {
            return userCredentialsService.updateUsers(userRequestDto);

        }

        throw new RuntimeException();
    }
}

