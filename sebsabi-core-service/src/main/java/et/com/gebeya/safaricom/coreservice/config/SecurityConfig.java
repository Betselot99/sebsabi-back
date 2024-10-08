package et.com.gebeya.safaricom.coreservice.config;

import et.com.gebeya.safaricom.coreservice.dto.WalletDto;
import et.com.gebeya.safaricom.coreservice.filter.RoleHeaderAuthenticationFilter;
import et.com.gebeya.safaricom.coreservice.filter.RoleHeaderAuthenticationProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.math.BigDecimal;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    public static final String ADMIN = "ROLE_[ADMIN]";
    public static final String CLIENT = "ROLE_[CLIENT]";
    public static final String GIGWORKER = "ROLE_[GIGWORKER]";
    public static final String[] UNAUTHORIZED_MATCHERS =
            {
                    "/swagger-ui/**",
                    "swagger-ui/index.html#/",
                    "/v3/api-docs/**",
                    "/api/core/client/signup",
                    "/api/core/gig-worker/signup",
                    "/api/core/calculate",
                    "/api/wallet/add-money"

            };
    public static final String[] ADMIN_MATCHERS = {
            "/api/core/admin/**",
    };
    public static final String[] CLIENT_MATCHERS =
            {
                    "/api/core/client/view/proposal",
                    "/api/core/client/view/profile",
                    "/api/core/client/view/profile/update",
                    "/api/core/client/view/form",
                    "/api/core/client/create/form",
                    "/api/core/client/create/form/add/question-to-form",
//                    "/api/core/client/view/forms/by_params",
                    "/api/core/client/view/form/proposal/**",
                    "/api/core/client/view/form/proposal/accept/**",
                    "/api/core/client/view/form/update",
                    "/api/core/client/search/form",
                    "/api/core/client/view/form/status/**",
                    "api/core/client/view/forms/completed/giveTestimonials",
                    "/api/core/client/view/form/progress/**",
                    "/api/core/client/view/form/update/questions/**",
                    "/api/core/client/view/form/completed/analyze",
                    "/api/core/client/pay",
                    "/api/core/client/check/wallet",
                    "/api/core/client/check/invoice",
                    "/api/core/client/check/wallet/add-money",
                    "/api/core/client/view/form/completed/analyze",
                    "/api/core/client/view/form/completed/analyze/download"
            };
    public static final String[] GIGWORKER_MATCHERS =
            {
                    "/api/core/gig-worker/view/profile",
                    "/api/core/gig-worker/view/profile/update",
                    "/api/core/gig-worker/view/forms/**",
                    "/api/core/gig-worker/search/form",
                    "/api/core/gig-worker/view/forms/proposal/submit",
                    "/api/core/gig-worker/view/forms/formQuestion/submit-response",
                    "/api/core/gig-worker/view/form/**",
                    "/api/core/gig-worker/check/balance",
                    "/api/core/gig-worker/check/wallet",
                    "/api/core/gig-worker/check/wallet/add-money",

            };


        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .cors(AbstractHttpConfigurer::disable)
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(request -> request.requestMatchers(UNAUTHORIZED_MATCHERS).permitAll())
                    .authorizeHttpRequests(request -> request.requestMatchers(ADMIN_MATCHERS).hasAuthority(ADMIN))
                    .authorizeHttpRequests(request -> request.requestMatchers(CLIENT_MATCHERS).hasAuthority(CLIENT))
                    .authorizeHttpRequests(request -> request.requestMatchers(GIGWORKER_MATCHERS).hasAuthority(GIGWORKER))

                    .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS)).exceptionHandling(handling -> {
                        handling.authenticationEntryPoint(unauthorizedEntryPoint());
                        handling.accessDeniedHandler(accessDeniedHandler());

                    })
                    .addFilterBefore(new RoleHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
//        @Bean
//        public SecurityFilterChain formAccessSecurityFilterChain(HttpSecurity http) throws Exception {
//            http
//                    .authorizeRequests(authorize -> authorize
//                            // Define the access rules for specific form endpoints
//                            .requestMatchers(HttpMethod.GET, "/api/core/gig-worker/forms/view/questionOfForm").access("@formAccessChecker.hasAccess(authentication, request)")
//                            // Add more rules as needed
//                            .anyRequest().authenticated()  // Authenticate any other requests
//                    )
//                    .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
//                    .exceptionHandling(handling -> {
//                        handling.authenticationEntryPoint(unauthorizedEntryPoint());
//                        handling.accessDeniedHandler(accessDeniedHandler());
//                    })
//                    .addFilterBefore(new RoleHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//
//            return http.build();
//        }
//    @Bean
//    public FormAccessChecker formAccessChecker() {
//        return new FormAccessChecker();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {

            //createAdminWallet();
            return new RoleHeaderAuthenticationProvider();
    }
    private void createAdminWallet(){
        WalletDto newUser = WalletDto.builder()
                .userId(0L)
                .amount(BigDecimal.valueOf(20000))
                .build();
    }


    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
