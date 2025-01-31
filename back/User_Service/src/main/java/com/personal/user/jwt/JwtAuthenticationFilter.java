package com.personal.user.jwt;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
        JwtService jwtService,
        UserDetailsService userDetailsService,
        HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
    	
    	//with refresh token n without cookies
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.substring(7).trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        }catch (io.jsonwebtoken.security.SignatureException |
                io.jsonwebtoken.MalformedJwtException |
                io.jsonwebtoken.ExpiredJwtException e) {
           // Specific JWT validation exceptions
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           response.getWriter().write("Invalid JWT token: " + e.getMessage());
       }catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    	
    	
//    	//with cookies, without refreshtoken
//    	System.out.println("JWT Filter triggered for URL: " + request.getRequestURL());
//    	Cookie[] cookies = request.getCookies();
//    	System.out.println("Cookies: " + (cookies != null ? Arrays.toString(cookies) : "null"));
//    	String token = null;
//    	String userEmail = null;
//    	UserDetails userDetails = null;
//    	if(cookies != null) {
//    		for(Cookie cookie : cookies) {
//    			System.out.println("Cookie found: " + cookie.getName() + "=" + cookie.getValue());
//    			if("jwt".equals(cookie.getName())) {
//    				token = cookie.getValue();
//    				break;
//    			}
//    		}
//    		userEmail = jwtService.extractUsername(token);
//    		userDetails =userDetailsService.loadUserByUsername(userEmail);
//    	}
//    	
//    	
//    	try {
//    		if(token != null && jwtService.isTokenValid(token, userDetails)) {
//    			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//    					userDetails,
//    					null,
//    					userDetails.getAuthorities()
//    					);
//    			
//    			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//    			SecurityContextHolder.getContext().setAuthentication(authToken);
//    		}
//    		filterChain.doFilter(request, response);			  		
//        } catch (Exception exception) {
//            handlerExceptionResolver.resolveException(request, response, null, exception);
//        }
    }
}
