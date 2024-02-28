package et.com.gebeya.safaricom.apigateway.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public Claims extractClaims(final String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            // Handle invalid token exception
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public List<String> extractRoles(final String token) {
        Claims claims = extractClaims(token);
        List<Map<String, String>> authorityList = claims.get("authority", List.class);
        return authorityList.stream()
                .map(authorityMap -> authorityMap.get("authority"))
                .collect(Collectors.toList());
    }

    public Long extractUserId(final String token) {
        Claims claims = extractClaims(token);
        // Extract user ID from claims or any other logic based on your token structure
        return claims.get("userID",Long.class); // Assuming the user ID is stored as the subject
    }

    public void validateToken(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            // Handle invalid token exception
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
