package org.cassettes.cassetteinventorysystem.controller;

import org.cassettes.cassetteinventorysystem.dto.LoginRequest;
import org.cassettes.cassetteinventorysystem.dto.UserResponse;
import org.cassettes.cassetteinventorysystem.entity.ResponseStructure;
import org.cassettes.cassetteinventorysystem.entity.User;
import org.cassettes.cassetteinventorysystem.repository.UserRepository;
import org.cassettes.cassetteinventorysystem.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity
                    .badRequest()
                    .body("Email already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
    
    @PostMapping("/api/login")
    public ResponseEntity<ResponseStructure<User>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

    	Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // Important to persist session
        securityContextRepository.saveContext(context, httpRequest, httpResponse);
        
        ResponseStructure<User> structure = new ResponseStructure<>();
        
        structure.setStatusCode(HttpStatus.OK.value());
        structure.setData(userRepository.findByEmail(request.getEmail()));
        structure.setMessage("User Logged In Successfully");
        
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
    
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/api/getSelf")
    public ResponseEntity<?> currentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        ));
    }
 

}
