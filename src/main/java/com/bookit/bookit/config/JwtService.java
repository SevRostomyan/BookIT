package com.bookit.bookit.config;

import com.bookit.bookit.entity.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.jws.soap.SOAPBinding;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//Purpose: The primary role of JwtService is to generate and validate JWT tokens.
// It handles the creation of tokens, setting claims (like username, roles, etc.),
// and validating these tokens (checking if they are expired, tampered with, etc.).
@Service
public class JwtService {
    private static final String SECRET_KEY = "TOTDo8keqi2YktPqhMxI5uIuYZhsWGC5v2X2haTFMd3ssEe0hijkQqERCXI2NrWDaL/VU+B6xvdX+fwkDYOwFptbGu5Lzb9LpaoyilF6heXI1jHFHQz/9cWuiCUs8SD/12t8xfXmFmmN6KZvCVgMQ427XBDFl/NsNxLmeRttMW9f+q/2Ifc6wl3XBWP55jGSwoyeUramVrro+JiT/oMUe+eYaIxItUrBgN3uK1m9CUPRUT65LL4kHZ/4ntgWAABLXkNdRqJ4/KdFsC3eliwVtpUGzp92QDXGF3BhXtPdJrFlfIrElBLU+FicS4uk+Ry0gnvvymgAHknH/CRg4T0Xc4W+AMryCZQuJevLHTUMeVc=\n";


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
}

/*public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
}*/

    //Modification of the above method to extract the userId also.
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserEntity) {
            UserEntity user = (UserEntity) userDetails;
            claims.put("userId", user.getId());
        }
        return generateToken(claims, userDetails);
    }


public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 20)) //Token går ut om 20 min för säkerhetsskäll.
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
}

    public Integer extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }


public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
}

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
