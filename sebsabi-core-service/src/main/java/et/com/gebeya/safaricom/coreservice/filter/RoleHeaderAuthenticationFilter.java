package et.com.gebeya.safaricom.coreservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RoleHeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
            String headerRole = request.getHeader("Authority");
            String roleId = request.getHeader("UserId");
            if (headerRole != null && roleId != null) {
                Authentication authentication = new RoleHeaderAuthenticationToken(headerRole, roleId);
                authentication = new RoleHeaderAuthenticationProvider().authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info(SecurityContextHolder.getContext().getAuthentication().toString());
            }


        chain.doFilter(request, response);
    }
}
