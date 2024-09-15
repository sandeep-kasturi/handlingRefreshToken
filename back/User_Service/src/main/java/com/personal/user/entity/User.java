package com.personal.user.entity;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

	private String name;
	
	@Id
	private String email;
	
	
	private String password;
	
	@Column(name = "activated")
	private boolean activated = true;
	
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role = Role.USER;
	
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// this is for only one user has one role												
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());				//for one user has multiple roles scenario use below one
        return Arrays.asList(authority);														//roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toList());
	}
	@Override
	public String getUsername() {
		
		return email;
	}
	@Override
	public String getPassword() {
		
		return password;
	}
	@Override
	public boolean isAccountNonExpired() {
		
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		
		return true;
	}
	@Override
	public boolean isEnabled() {
		
		return activated;
	}
}
