package et.com.gebeya.safaricom.coreservice.filter;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class RoleHeaderAuthenticationToken extends AbstractAuthenticationToken {
    private String headerRole;
    private Integer roleId;

    public RoleHeaderAuthenticationToken(String headerRole, String roleId) {
        super(null);
        if(headerRole!=null && roleId!=null){
            this.headerRole = headerRole;
            this.roleId= Integer.valueOf(roleId);
            setAuthenticated(false);
        }
    }

    @Override
    public Object getCredentials() {
        return roleId;
    }

    @Override
    public Object getPrincipal() {
        return headerRole;
    }

}
