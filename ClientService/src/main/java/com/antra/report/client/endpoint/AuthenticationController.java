package com.antra.report.client.endpoint;

import com.antra.report.client.entity.User;
import com.antra.report.client.pojo.reponse.SigninResponse;
import com.antra.report.client.pojo.reponse.SignupResponse;
import com.antra.report.client.pojo.reponse.ValidateResponse;
import com.antra.report.client.pojo.request.SigninRequest;
import com.antra.report.client.pojo.request.SignupRequest;
import com.antra.report.client.pojo.request.ValidateRequest;
import com.antra.report.client.repository.UserRepo;
import com.antra.report.client.security.JwtTokenUtil;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtil tokenProvider;

    @PostMapping("/validate")
    public ResponseEntity<?> validateUser(@Valid @RequestBody ValidateRequest validateRequest) {
        if (tokenProvider.validateToken(validateRequest.getToken())) {
            return ResponseEntity.ok(new ValidateResponse(true));
        } else {
            return ResponseEntity.ok(new ValidateResponse(false));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninRequest signinRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new SigninResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepo.existsByEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>(new SignupResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));
        userRepo.save(user);

        return ResponseEntity.ok(new SignupResponse(true, "User registered successfully"));
    }
}
