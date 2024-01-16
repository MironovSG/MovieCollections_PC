package ru.mironov.moviecollections.service;

import ru.mironov.moviecollections.dto.UserDto;
import ru.mironov.moviecollections.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByUsername(String username);

    List<UserDto> findAllUsers();

    void addRoleToUser(Long userId, Long roleId);

    Long getCurUserId();

    String getCurUserRole();
}
