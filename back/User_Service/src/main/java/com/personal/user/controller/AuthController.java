package com.personal.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personal.user.entity.User;
import com.personal.user.jwt.AuthenticationResponse;
import com.personal.user.jwt.AuthenticationService;
import com.personal.user.jwt.JwtService;
import com.personal.user.jwt.LoginUserDto;
import com.personal.user.repository.UserRepository;
import com.personal.user.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private JwtService jwtService;  
	
	private final UserDetailsService userDetailsService;
	
	public AuthController(UserDetailsService userDetailsService ) {
		this.userDetailsService = userDetailsService;
	}
	
	

	
	
//	@PostMapping("/signup")
//    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
//        User registeredUser = authenticationService.signup(registerUserDto);
//
//        return ResponseEntity.ok(registeredUser);
//    }
	
	@PostMapping("/signup")
	public ResponseEntity<Object> add(@RequestBody User user) throws Exception{

			userService.add(user);	
			return new ResponseEntity<Object>("successfully user added", HttpStatus.OK);
	}
	
	
//	//this is for without refresh token
//	@PostMapping("/login")
//	public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) throws InvalidCredentialsException {
//	  User authenticatedUser = authenticationService.authenticate(loginUserDto);
//
//	  String accessToken = jwtService.generateToken(authenticatedUser);
//
//	  LoginResponse loginResponse = new LoginResponse(accessToken, jwtService.getExpirationTime());
//
//	  return ResponseEntity.ok(loginResponse);
//	}
	
	
	
	
	
	
//	//with refresh token n without cookies
//	
//	@PostMapping("/login")
//	public ResponseEntity<Object> authenticate(@RequestBody LoginUserDto loginUserDto) throws Exception {
//		try {
//			AuthenticationResponse authResponse = authenticationService.authenticate(loginUserDto);
//			System.out.println("auth response: "+ authResponse);
//			return new ResponseEntity<Object>(authResponse, HttpStatus.OK);			
//		} catch (Exception e) {
//			return new ResponseEntity<Object>("error Invalid cred",HttpStatus.UNAUTHORIZED);
//		}
//	}
	
	
	
//	//without refreshtoken with cookies
//	@PostMapping("/login")
//	public ResponseEntity<Object> authenticate(@RequestBody LoginUserDto loginUserDto, HttpServletResponse response) throws Exception {
//		try {
//			AuthenticationResponse authResponse = authenticationService.authenticate(loginUserDto);
//			System.out.println("auth response: "+ authResponse);
//			
//			//setting up cookies
//			Cookie cookie = new Cookie("jwt", authResponse.getAccessToken());
//			cookie.setHttpOnly(true);
//			cookie.setPath("/");
//			cookie.setMaxAge(86400);
//			cookie.setDomain("localhost");
////			cookie.setSameSite("Strict");
//			cookie.setSecure(false);
////			cookie.setAttribute("SameSite", "None");
//			
//			
////			response.addCookie(cookie);
////			response.setHeader("Set-Cookie", "jwt=" + authResponse.getAccessToken() + "; HttpOnly; Path=/; Max-Age=86400; SameSite=Lax");
//			response.addHeader("Set-Cookie", String.format("%s;, SameSite=None", cookie.toString()));
//			
//			System.out.println("Cookie set: " + cookie.getName() + "=" + cookie.getValue());
//	        System.out.println("Cookie properties: HttpOnly=" + cookie.isHttpOnly() + ", Path=" + cookie.getPath() + ", MaxAge=" + cookie.getMaxAge() + ", Domain=" + cookie.getDomain() + ", Secure=" + cookie.getSecure());
//			
//			return new ResponseEntity<Object>(authResponse, HttpStatus.OK);			
//		} catch (Exception e) {
//			return new ResponseEntity<Object>("error Invalid cred",HttpStatus.UNAUTHORIZED);
//		}
//	}
	
	
	//with refresh token with cookies
	@PostMapping("/login")
	public ResponseEntity<Object> authenticate(@RequestBody LoginUserDto loginUserDto, HttpServletResponse response) throws Exception {
		try {
			log.info("login method is invoked");
			AuthenticationResponse authResponse = authenticationService.authenticate(loginUserDto);
			System.out.println("auth response: "+ authResponse);
			
			ResponseCookie refreshCookie =ResponseCookie.from("refreshToken",authResponse.getRefreshToken())
					.httpOnly(true)
					.secure(false)
					.path("/")
					.maxAge(24*60*60)
					.sameSite("None")
					.build();
			response.addHeader("Set-Cookie", refreshCookie.toString());
//			System.out.println("cookie:"+ refreshCookie.toString()+"resp:" + response.getHeader(refreshCookie.toString()));
			return new ResponseEntity<Object>(authResponse,HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<Object>("error Invalid cred",HttpStatus.UNAUTHORIZED);
		}
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<Object> refreshToken(@CookieValue(value = "refreshToken",required = true) String refreshToken){
		log.info("refresh-token method is invoked");
		log.info("refresh token ***********:"+refreshToken);
		//if refresh token is valid then no need to check for Authentication obj
		if(refreshToken != null && jwtService.validateToken(refreshToken)) {
			String userEmail = jwtService.extractUsername(refreshToken);
			//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (userEmail != null) {
	            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
	            String newAccessToken = jwtService.generateToken(userDetails);
	            return new ResponseEntity<Object>(newAccessToken, HttpStatus.OK);
			}
			return new ResponseEntity<Object>("email not found with the refresh token", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Object>("refresh token is expired, pls login again", HttpStatus.UNAUTHORIZED);
	}
	
	
	@GetMapping("/logout")
	public ResponseEntity<Object> logout(HttpServletResponse response){
		try {
			Cookie cookie = new Cookie("jwt", null);
			cookie.setHttpOnly(true);
			cookie.setPath("/");
			cookie.setMaxAge(0);
			
			response.addCookie(cookie);
			return new ResponseEntity<Object>("logout successful", HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<Object>("logout failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
