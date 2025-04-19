package ru.screbber.stockSimulator.service;

import ru.screbber.stockSimulator.entity.UserEntity;

public interface UserService {

    void registerUser(String username, String email, String password) throws Exception;

    boolean confirmEmail(String token);

    UserEntity findByUsername(String username);
}
