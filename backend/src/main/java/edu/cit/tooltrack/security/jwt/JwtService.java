package edu.cit.tooltrack.security.jwt;

import edu.cit.tooltrack.dto.LoginRequest;
import edu.cit.tooltrack.dto.UserResponseDTO;
import edu.cit.tooltrack.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final long EXPIRATION_TIME = 30 * 60 * 1000;
    private static final String SECRET_KEY = "dacc38805188690486da9a74e03510f16ce0f81a966391d351a9f6928d2d85cae650b1daf140f7f29ae9b3423af142db146889c979f04dc70ad502f4d6c8074b";

    public static String generateToken(UserResponseDTO user) {
        return Jwts
                .builder()
                .claim("name", user.getFirst_name() + " " + user.getLast_name())
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .issuer("Spring-boot")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(generateKeyFromBase64())
                .compact();
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimResolver) {
        Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }

    private static SecretKey generateKeyFromBase64() {
        byte[] decode = Decoders.BASE64.decode(JwtService.SECRET_KEY);
        return Keys.hmacShaKeyFor(decode);
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(generateKeyFromBase64())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpirations(token).before(new Date());
    }

    private Date extractExpirations(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}
