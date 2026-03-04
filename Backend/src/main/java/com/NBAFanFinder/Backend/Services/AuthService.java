package com.NBAFanFinder.Backend.Services;

import com.NBAFanFinder.Backend.DTOs.AuthResponse;
import com.NBAFanFinder.Backend.DTOs.LoginRequest;
import com.NBAFanFinder.Backend.DTOs.RegisterRequest;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Exceptions.UnauthorizedException;
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
        if (request.email() == null || request.password() == null) {
            throw new IllegalArgumentException("Email et mot de passe sont obligatoires");
        }

        Optional<User> userOptional = userRepository.findByEmail(request.email());

        if (userOptional.isEmpty()) {
            throw new UnauthorizedException("Email ou mot de passe incorrect");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Email ou mot de passe incorrect");
        }

        if (!user.getActif()) {
            throw new UnauthorizedException("Compte désactivé");
        }

        return generateAuthResponse(user);
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.nom() == null || request.prenom() == null ||
            request.email() == null || request.password() == null) {
            throw new IllegalArgumentException("Tous les champs sont obligatoires");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        User newUser = new User(
            request.nom(),
            request.prenom(),
            request.email(),
            request.password()
        );

        if (request.dateNaissance() != null) {
            newUser.setDateNaissance(request.dateNaissance());
        }

        newUser.setPassword(passwordEncoder.encode(request.password()));

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
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé : " + id));
        user.setActif(true);
        userRepository.save(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(user.getId(), token, user.getEmail(), user.getNom(), user.getPrenom());
    }
}
