package com.personal.user.jwt;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.personal.user.entity.User;
import com.personal.user.repository.UserRepository;

@Component
public class AuthenticationService {

	private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;
    
    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
        ) {
            this.authenticationManager = authenticationManager;
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }
    
    
    
  @Autowired
  private JwtService jwtService;
  
  public AuthenticationResponse authenticate(LoginUserDto input) throws Exception {
	  try {
		  Authentication authentication = authenticationManager.authenticate(
				  new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
		  System.out.println(authentication.isAuthenticated());
		  User user = userRepository.findByEmail(input.getEmail()).orElseThrow();
		  String accessToken = jwtService.generateToken(user);
		  String refreshToken = jwtService.generateRefreshToken(user);
		  
		  return new AuthenticationResponse(accessToken, refreshToken, jwtService.getExpirationTime());		
	} catch (Exception e) {
		// TODO: handle exception
		throw new Exception("Invalid user cred");
	}
  }
  
//  public AuthenticationResponse refreshToken(String refreshToken) {
//      Optional<RefreshToken> refreshTokenOptional = jwtService.findRefreshToken(refreshToken);
//      if (refreshTokenOptional.isPresent()) {
//          RefreshToken token = refreshTokenOptional.get();
//          if (!token.getExpiryDate().isBefore(Instant.now())) {
//              String email = token.getUser().getEmail();
//              User user = userRepository.findByEmail(email).orElseThrow();
//              String accessToken = jwtService.generateToken(user);
//              jwtService.deleteRefreshToken(token);
//              String newRefreshToken = jwtService.generateRefreshToken(user);
//              return new AuthenticationResponse(accessToken, newRefreshToken, jwtService.getExpirationTime());
//          }
//      }
//      return null;
//  }
}
