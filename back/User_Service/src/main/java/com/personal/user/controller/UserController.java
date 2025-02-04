package com.personal.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personal.user.entity.User;
import com.personal.user.jwt.JwtService;
import com.personal.user.service.UserService;

@RestController
@RequestMapping("/usr")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtService jwtService;
	
	private UserDetailsService userDetailsService;
	
	public UserController(UserDetailsService userDetailsService ) {
		this.userDetailsService = userDetailsService;
	}
	
	@GetMapping("/profile") 
	public ResponseEntity<Object> getUserByJwt(@RequestHeader("Authorization") String jwt) throws Exception{
			User user = userService.getUserByJwt(jwt);
			return new ResponseEntity<Object>(user,HttpStatus.OK);
	}
	
	//for cookie
	@GetMapping("/getMsg")
	public ResponseEntity<Object> getMsg(@RequestHeader("Authorization") String jwt){
		System.out.println("jwt:" + jwt);
		final String userEmail = jwtService.extractUsername(jwt.substring(7));
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
		if(userEmail != null && jwtService.isTokenValid(jwt.substring(7),userDetails)) {
			return new ResponseEntity<Object>("Side Operation Success", HttpStatus.OK);			
		}
		return new ResponseEntity<Object>("Side Operation failed", HttpStatus.UNAUTHORIZED);
	}
	
	
}
