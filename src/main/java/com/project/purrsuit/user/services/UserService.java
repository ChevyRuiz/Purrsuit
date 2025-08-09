package com.project.purrsuit.user.services;

import com.project.purrsuit.response.Response;
import com.project.purrsuit.user.dtos.UserDTO;
import com.project.purrsuit.user.entity.User;

import java.util.List;

public interface UserService {
    User getCurrentLoggedInUser();

    Response<List<UserDTO>> getAllUsers();

    Response<UserDTO> getOwnAccountDetails();
}
