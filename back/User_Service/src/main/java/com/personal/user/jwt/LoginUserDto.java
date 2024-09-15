package com.personal.user.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserDto {
	
    private String email;
    
    private String password;
    
    // getters and setters here...
}