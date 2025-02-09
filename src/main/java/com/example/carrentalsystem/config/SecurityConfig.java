package com.example.carrentalsystem.config;

import com.example.carrentalsystem.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthService authService;
    private final String SECRET_KEY = "f0cb7a4b2c0a6b812ed1d16f0c4327e6d27e392b8b5f9a671ff07f13e9fcd9f3"; // Replace with a secure key

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll() // Public endpoints
                .requestMatchers("/cars/**").permitAll()   
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/auth/register/**").permitAll()
                .requestMatchers("/auth/login/**").permitAll()
                .requestMatchers("/signup.html/**").permitAll()
                .requestMatchers("/login.html/**").permitAll()
                .requestMatchers("/addcar.html/**").permitAll()
                .requestMatchers("/manage.html/**").permitAll()
                .requestMatchers("/bookings/**").permitAll()
                .requestMatchers("/index.html/**").permitAll()
                .requestMatchers("/style.css/**").permitAll()
                .requestMatchers("/img/**").permitAll()
                .requestMatchers("/**").permitAll()
//                .requestMatchers("/js/**").permitAll()
              
//                .anyRequest().authenticated() // All other endpoints require authentication
            )
            .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

        
    	
    	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    	        throws IOException, ServletException {
    	    try {
    	        System.out.println("JwtAuthenticationFilter: Filter triggered");

    	        String token = getTokenFromRequest(request);
    	        if (token != null) {
    	            System.out.println("Token found: " + token);

    	            Claims claims = parseToken(token);
    	            if (claims != null) {
    	                String email = claims.getSubject();
    	                String role = claims.get("role", String.class);

    	                System.out.println("Parsed token. Email: " + email + ", Role: " + role);

    	                if (email != null && role != null) {
    	                    UsernamePasswordAuthenticationToken authentication =
    	                            new UsernamePasswordAuthenticationToken(email, null,
    	                                    List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    	                    SecurityContextHolder.getContext().setAuthentication(authentication);

    	                    System.out.println("Authentication set: " + SecurityContextHolder.getContext().getAuthentication());
    	                } else {
    	                    System.out.println("Email or role is missing in the token");
    	                }
    	            } else {
    	                System.out.println("Token parsing failed");
    	            }
    	        } else {
    	            System.out.println("No token found in the Authorization header");
    	        }
    	        chain.doFilter(request, response);
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        System.out.println("Exception in JwtAuthenticationFilter: " + e.getMessage());
    	        throw e;
    	    }
    	}


        private String getTokenFromRequest(HttpServletRequest request) {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                System.out.println("Authorization header found: " + authorizationHeader);
                return authorizationHeader.substring(7); // Remove "Bearer " prefix
            } else {
                System.out.println("No Authorization header found or doesn't start with 'Bearer '.");
            }
            return null;
        }

        private Claims parseToken(String token) {
            try {
                return Jwts.parserBuilder()
                        .setSigningKey(SECRET_KEY.getBytes()) // Use the same secret key as token generation
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } catch (Exception e) {
                e.printStackTrace(); // Log the exception for debugging
                System.out.println("Token parsing failed: " + e.getMessage());
                return null; // Return null if the token is invalid
            }
        }
    }
}
