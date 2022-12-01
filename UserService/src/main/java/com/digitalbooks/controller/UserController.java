package com.digitalbooks.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbooks.model.User;

@RestController
@RequestMapping
public class UserController {
	
	@GetMapping("/signup")
	public User registerUser(){
		return new User();
		
	}
}
