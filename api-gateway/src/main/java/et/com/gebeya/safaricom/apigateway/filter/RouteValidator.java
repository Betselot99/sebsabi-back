package et.com.gebeya.safaricom.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/register",
            "/api/core/our-websocket",
            "/api/auth/login",
            "/api/core/client/signup",
            "/api/core/gig-worker/signup",
            "/api/auth/validate",
            "/api/auth/reset-password/**",
            "/api/auth/reset/update-password",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/swagger-resources/**",
            "/v3/api-docs/swagger-config"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
