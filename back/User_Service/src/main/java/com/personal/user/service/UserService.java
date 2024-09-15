package com.personal.user.service;

import com.personal.user.entity.User;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

	public User getUserByJwt(String jwt) throws Exception;

	public void add(User user) throws Exception;

}
