package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class RegistrationController {

	@GetMapping(path="/register")
	public String register() {
		log.info("inside get method");
		return "welcome to regster service";
	}
	
	@PostMapping("/signUp")
	public Student signUp(@RequestBody Student student) {
		log.info("inside post method");
		return student;
	}
}
