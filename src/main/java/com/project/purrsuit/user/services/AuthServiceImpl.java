package com.project.purrsuit.user.services;

import com.project.purrsuit.exceptions.BadRequestException;
import com.project.purrsuit.exceptions.NotFoundException;
import com.project.purrsuit.response.Response;
import com.project.purrsuit.security.JwtUtils;
import com.project.purrsuit.user.dtos.LoginRequest;
import com.project.purrsuit.user.dtos.LoginResponse;
import com.project.purrsuit.user.dtos.RegistrationRequest;
import com.project.purrsuit.user.entity.User;
import com.project.purrsuit.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    @Override
    public Response<?> register(RegistrationRequest registrationRequest) {
        log.info("Inside register()");

        if (userRepository.existsByUsername(registrationRequest.getUsername())){
            throw new BadRequestException("Email already exists");
        }

        User userToSave = User.builder()
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(userToSave);

        log.info("user registered successfully");

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User registered successfully")
                .build();
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {
        log.info("INSIDE login()");

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("Invalid Email"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadRequestException("Invalid password");
        }

        String token = jwtUtils.generateToken(user.getUsername());


        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setToken(token);

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .data(loginResponse)
                .build();
    }
}
