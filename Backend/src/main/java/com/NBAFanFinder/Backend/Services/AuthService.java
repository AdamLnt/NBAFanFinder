package com.NBAFanFinder.Backend.Services;

import com.NBAFanFinder.Backend.DTOs.AuthResponse;
import com.NBAFanFinder.Backend.DTOs.LoginRequest;
import com.NBAFanFinder.Backend.DTOs.RegisterRequest;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new RuntimeException("Email et mot de passe sont obligatoires");
        }

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        if (!user.getActif()) {
            throw new RuntimeException("Compte désactivé");
        }

        return generateAuthResponse(user);
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.getNom() == null || request.getPrenom() == null ||
            request.getEmail() == null || request.getPassword() == null) {
            throw new RuntimeException("Tous les champs sont obligatoires");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        User newUser = new User(
            request.getNom(),
            request.getPrenom(),
            request.getEmail(),
            request.getPassword()
        );

        if (request.getDateNaissance() != null) {
            newUser.setDateNaissance(request.getDateNaissance());
        }

        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        newUser.setActif(false);

        User savedUser = userRepository.save(newUser);

        return new AuthResponse(
            savedUser.getId(),
            null, 
            savedUser.getEmail(),
            savedUser.getNom(),
            savedUser.getPrenom()
        );
    }

    public void activate(Long id) {
        if (!userRepository.existsById(id)) { 
            throw new RuntimeException("Utilisateur non trouvé : " + id); 
        } 
        User user = userRepository.findById(id).get(); 
        user.setActif(true); 
        userRepository.save(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(user.getId(), token, user.getEmail(), user.getNom(), user.getPrenom());
    }
}
