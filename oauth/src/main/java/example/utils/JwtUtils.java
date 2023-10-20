package example.utils;

import example.models.User;
import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;

public class JwtUtils {
    private final static String SECRET_KEY = "seergerbgerervvread13421r42341223er2qdaser23412312321easdf234312ecret";
    private final static SignatureAlgorithm sa = SignatureAlgorithm.HS256;
    private final static SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), sa.getJcaName());
    private final static JwtParser parser =  Jwts.parserBuilder().setSigningKey(secretKeySpec).build();

    public static String generateToken(User user, Timestamp expiresAt){
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(expiresAt)
                .signWith(secretKeySpec)
                .compact();
    }

    public static Claims getClaimsFromToken(String token){
        return (Claims) parser.parse(token).getBody();
    }
}
