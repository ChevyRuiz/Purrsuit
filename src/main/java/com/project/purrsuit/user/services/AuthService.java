package com.project.purrsuit.user.services;


import com.project.purrsuit.response.Response;
import com.project.purrsuit.user.dtos.LoginRequest;
import com.project.purrsuit.user.dtos.LoginResponse;
import com.project.purrsuit.user.dtos.RegistrationRequest;

public interface AuthService {
    Response<?> register(RegistrationRequest registrationRequest);
    Response<LoginResponse> login(LoginRequest loginRequest);
}
