package ru.screbber.stockSimulator.service;

public interface UserService {

    void registerUser(String username, String email, String password) throws Exception;

    boolean confirmEmail(String token);
}
