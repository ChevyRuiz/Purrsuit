package com.project.purrsuit.user.services;

import com.project.purrsuit.response.Response;
import com.project.purrsuit.user.dtos.UserDTO;
import com.project.purrsuit.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    @Override
    public User getCurrentLoggedInUser() {
        return null;
    }

    @Override
    public Response<List<UserDTO>> getAllUsers() {
        return null;
    }

    @Override
    public Response<UserDTO> getOwnAccountDetails() {
        return null;
    }
}
