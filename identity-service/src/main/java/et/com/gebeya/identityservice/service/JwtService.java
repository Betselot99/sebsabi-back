package et.com.gebeya.identityservice.service;

import et.com.gebeya.identityservice.dto.requestDto.TokenDto;
import et.com.gebeya.identityservice.dto.responseDto.ValidationResponseDto;
import et.com.gebeya.identityservice.entity.UserCredentials;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;


import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    //@Value("${token.signing.key}")
    private String jwtSigningKey="5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public String generateToken(UserCredentials users) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authority", users.getAuthorities());
        claims.put("userID", users.getUserId());
        return generateToken(claims, users);
    }



    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserCredentials users) {
        long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000;
        // Set expiration time to three days from the current time
        Date expirationTime = new Date(System.currentTimeMillis() + threeDaysInMillis);

        return Jwts.builder().setClaims(extraClaims)
                .setSubject(users.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationTime)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public boolean isTokenValid(String token, UserCredentials users) {
        final String userName = extractUserName(token);
        return (userName.equals(users.getUsername())) && !isTokenExpired(token);
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
