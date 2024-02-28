package et.com.gebeya.safaricom.coreservice.util.constants.SecurityConstants;

public final class SecurityConstants {
    public static final String UNAUTHORIZED_MATCHERS = "/swagger-ui/**";
    public static final String ADMIN_MATCHERS = "/api/core/**";
    public static final String CLIENTS_MATCHERS = "/api/core/clients/**";
    public static final String GIGWORKER_MATCHERS = "/api/core/gig-worker/**";

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }

    public static final String ADMIN = "ADMIN";
    public static final String GIGWORKER = "Gigworker";
    public static final String CLIENT = "Client";

}

