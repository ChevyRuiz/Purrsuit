package com.project.purrsuit.user.services;

import com.project.purrsuit.exceptions.NotFoundException;
import com.project.purrsuit.response.Response;
import com.project.purrsuit.user.dtos.UserDTO;
import com.project.purrsuit.user.entity.User;
import com.project.purrsuit.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public User getCurrentLoggedInUser() {

        log.info("inside getCurrentLoggedInUser()");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User was not found"));
    }

    @Override
    public Response<List<UserDTO>> getAllUsers() {
        log.info("inside getAllUsers)");

        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<UserDTO> userDTOS = users
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();

        return Response.<List<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("all users retrieved successfully")
                .data(userDTOS)
                .build();
    }

    @Override
    public Response<UserDTO> getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user was not found"));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("user information retrieved successfully")
                .data(userDTO)
                .build();
    }


    @Override
    public Response<UserDTO> getOwnAccountDetails() {
        log.info("inside getOwnAccountDetails()");

        User user = getCurrentLoggedInUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("user information retrieved successfully")
                .data(userDTO)
                .build();
    }
}
