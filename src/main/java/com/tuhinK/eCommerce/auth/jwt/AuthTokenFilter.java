package com.tuhinK.eCommerce.auth.jwt;

import com.tuhinK.eCommerce.auth.user.EComUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private EComUserDetailsService eComUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);
            if (StringUtils.hasText(jwt) && jwtUtility.validateJwtToken(jwt)) {

                String username = jwtUtility.getUsernameFromJwtToken(jwt);
                UserDetails userDetails = eComUserDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                // Builds Spring Security's authentication object using the user, and sets it in the
                // security context, allowing the user to be treated as logged in throughout the app.
            }
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage() +" : Invalid or expired token, you may login and try again!");
            return;
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
            return;

        }

        filterChain.doFilter(request, response);
        // “I've done my work. Now pass the request and response to the next filter in the chain
        // (or ultimately the controller).”
        // Without this line, the request would never reach your controller or further filters.
        // The request would just stop at your custom filter, and the response would hang or time out.
    }

    private String parseJwt(HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");

            // Check if the Authorization header is present and starts with "Bearer "
            if (StringUtils.hasText(header)) {
                String[] parts = header.split(" ");

                // Check if there are multiple Bearer tokens
                if (parts.length > 2 || (parts.length==2 && !parts[0].equals("Bearer"))) {
                    throw new IllegalArgumentException("Invalid Authorization header: multiple Bearer tokens detected.");
                }

                // Ensure the first part is "Bearer"
                if (parts[0].equals("Bearer")) {
                    return parts[1].trim(); // Return the token, trimming any whitespace
                }
            }

            return null;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }
}
