package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.entity.UserEntity;
import ru.screbber.stockSimulator.entity.VerificationTokenEntity;
import ru.screbber.stockSimulator.repository.UserRepository;
import ru.screbber.stockSimulator.repository.VerificationTokenRepository;
import ru.screbber.stockSimulator.service.MailSenderService;
import ru.screbber.stockSimulator.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;
    private final MailSenderService mailSender;

    @Override
    public void registerUser(String username, String password) throws Exception {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username is already taken.");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);
    }

    @Override
    public void registerUser(String username, String email, String rawPassword) throws Exception {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username is already taken.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email is already in use.");
        }

        // Создаем пользователя
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setEmailVerified(false);
        userRepository.save(newUser);

        // Генерируем токен
        String token = UUID.randomUUID().toString();
        VerificationTokenEntity vte = new VerificationTokenEntity();
        vte.setToken(token);
        vte.setUser(newUser);
        vte.setExpiryDate(LocalDateTime.now().plusDays(1));
        tokenRepository.save(vte);

        // Отправляем письмо
        sendVerificationEmail(newUser, token);
    }

    private void sendVerificationEmail(UserEntity user, String token) {
        String confirmUrl = "http://localhost:8080/confirm?token=" + token;
        String text = "Hello, " + user.getUsername() +
                "! \n\nTo confirm your registration in StockSimulator, please click the link below:\n" +
                confirmUrl + "\n\nThis link will expire in 24 hours.";
        mailSender.sendAsync(user.getEmail(), "Email Verification", text);
    }

    @Override
    public boolean confirmEmail(String token) {
        Optional<VerificationTokenEntity> vteOpt = tokenRepository.findByToken(token);
        if (vteOpt.isEmpty()) {
            return false;
        }
        VerificationTokenEntity vte = vteOpt.get();

        if (vte.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        UserEntity user = vte.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        tokenRepository.delete(vte);
        return true;
    }
}
