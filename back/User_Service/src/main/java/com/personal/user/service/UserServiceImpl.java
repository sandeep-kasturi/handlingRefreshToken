package com.personal.user.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.personal.user.entity.User;
import com.personal.user.jwt.JwtService;
import com.personal.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private JwtService jwtService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public User getUserByJwt(String jwt) throws Exception {
		try {
			String email = jwtService.extractUsername(jwt.substring(7));
			if(email.isBlank()) {
				throw new Exception("there is no users n email associated with the provided jwt");
			}
			Optional<User> user = userRepository.findByEmail(email);
			if(!user.isPresent()) {
				throw new Exception("there is no user data associated with the email");
			}
			return user.get();
			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public void add(User user) throws Exception {
		try {
			//user validation will be done in f-end
			User newUser = new User();
			BeanUtils.copyProperties(user, newUser);
			newUser.setPassword(passwordEncoder.encode(user.getPassword()));
			
			userRepository.save(newUser);
				
		} catch (Exception e) {
			throw new Exception("user isn't saved");
		}
		
	}

}
