package com.vitalpet.msauth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    //Esta es la clase que fabrica emite y valida los tokens.

    //Esto en produccion se supone que va en el aplication.yml
    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(Long userId, String role){
        return Jwts.builder()
                .setSubject(userId.toString()) //El dueño del token
                .claim("role",role) //guardamos el rol como dato extra (payload)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) //Expira en 10 horas
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) //firmamos con la secret key "matemáticamente"
                .compact();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e){
            return false; //Si ya está expirado, cae aca.
        }
    }

}
