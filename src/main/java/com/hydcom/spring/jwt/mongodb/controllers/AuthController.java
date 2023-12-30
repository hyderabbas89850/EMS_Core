package com.hydcom.spring.jwt.mongodb.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hydcom.spring.jwt.mongodb.models.ERole;
import com.hydcom.spring.jwt.mongodb.models.Role;
import com.hydcom.spring.jwt.mongodb.models.User;
import com.hydcom.spring.jwt.mongodb.payload.request.LoginRequest;
import com.hydcom.spring.jwt.mongodb.payload.request.SignupRequest;
import com.hydcom.spring.jwt.mongodb.payload.response.JwtResponse;
import com.hydcom.spring.jwt.mongodb.payload.response.ResponseMessage;
import com.hydcom.spring.jwt.mongodb.repository.RoleRepository;
import com.hydcom.spring.jwt.mongodb.repository.UserRepository;
import com.hydcom.spring.jwt.mongodb.security.jwt.JwtUtils;
import com.hydcom.spring.jwt.mongodb.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		ResponseMessage res = new  ResponseMessage();
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);
			
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
			JwtResponse jwtRes = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
			res.setStatus(1);
			res.setData(jwtRes);
			res.setMessage(loginRequest.getUsername()+" login Successfully");
		}catch (Exception e) {
			res.setStatus(0);
			res.setMessage(loginRequest.getUsername()+"  Bad credentials");
			//e.printStackTrace();
		}
		
		
		return ResponseEntity.ok(res);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		ResponseMessage res = new ResponseMessage();
		try {
			if (userRepository.existsByEmail(signUpRequest.getEmail())) {
				res.setStatus(0);
				res.setMessage(signUpRequest.getEmail()+" Already in use.");
				return ResponseEntity.ok().body(res);
			}
			// Create new user's account
			User user = new User(signUpRequest.getEmail(),signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()), signUpRequest.getFirstName(),signUpRequest.getLastName(), signUpRequest.getMobile(),signUpRequest.getAddress());
			user.setUserId(UUID.randomUUID().toString());
			Set<String> strRoles = signUpRequest.getRoles();
			Set<Role> roles = new HashSet<>();
			if (strRoles == null) {
				Role userRole = roleRepository.findByName(ERole.ROLE_USER)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				roles.add(userRole);
			} else {
				strRoles.forEach(role -> {
					switch (role) {
					case "admin":
						Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(adminRole);
						break;
					case "mod":
						Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(modRole);
						break;
					default:
						Role userRole = roleRepository.findByName(ERole.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(userRole);
					}
				});
			}

			user.setRoles(roles);
			userRepository.save(user);
			
			res.setStatus(1);
			res.setMessage("User "+signUpRequest.getEmail()+" Registered Successfully.");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(res);
	}
}
