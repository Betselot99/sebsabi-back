package et.com.gebeya.identityservice.config;

import et.com.gebeya.identityservice.entity.Authority;
import et.com.gebeya.identityservice.entity.UserCredentials;
import et.com.gebeya.identityservice.filter.JwtAuthFilter;
import et.com.gebeya.identityservice.service.UserCredentialsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class AuthConfig {


    private final JwtAuthFilter jwtAuthFilter;
    private final UserCredentialsService usersService;

    public AuthConfig(JwtAuthFilter jwtAuthFilter, UserCredentialsService usersService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.usersService = usersService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.anyRequest().permitAll())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedEntryPoint()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }


    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    private void createAdminUser() {
        try {
            UserCredentials users = usersService.loadUserByUsername("admin");
        } catch (Exception e) {
            UserCredentials users = UserCredentials.builder()
                    .name("Admin Admin")
                    .isActive(true)
                    .authority(Authority.ADMIN)
                    .userName("admin")
                    .userId(0L)
                    .password(passwordEncoder().encode("password"))
                    .build();
            usersService.createUpdateUser(users);
        }
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usersService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        createAdminUser();
        return authProvider;
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}