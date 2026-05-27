package com.NBAFanFinder.Backend.Services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.NBAFanFinder.Backend.DTOs.AddressRequest;
import com.NBAFanFinder.Backend.DTOs.AuthResponse;
import com.NBAFanFinder.Backend.DTOs.LoginRequest;
import com.NBAFanFinder.Backend.DTOs.RegisterRequest;
import com.NBAFanFinder.Backend.Entities.Address;
import com.NBAFanFinder.Backend.Entities.Team;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Exceptions.UnauthorizedException;
import com.NBAFanFinder.Backend.Repositories.AddressRepository;
import com.NBAFanFinder.Backend.Repositories.TeamRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                      AddressRepository addressRepository,
                      TeamRepository teamRepository,
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.teamRepository = teamRepository;
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

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        User newUser = new User(
            request.nom(),
            request.prenom(),
            request.email(),
            passwordEncoder.encode(request.password())
        );

        if (request.dateNaissance() != null) {
            newUser.setDateNaissance(request.dateNaissance());
        }

        newUser.setActif(false);
        newUser.setActivationToken(UUID.randomUUID().toString());

        attachTeams(newUser, request.equipesSupporteesIds());

        User savedUser = userRepository.save(newUser);

        Address address = buildAddress(request.adresse(), savedUser);
        addressRepository.save(address);

        return new AuthResponse(
            savedUser.getId(),
            null,
            savedUser.getEmail(),
            savedUser.getNom(),
            savedUser.getPrenom(),
            savedUser.getActivationToken()
        );
    }

    public void activate(String token) {
        User user = userRepository.findByActivationToken(token)
            .orElseThrow(() -> new NotFoundException("Token d'activation invalide ou déjà utilisé"));
        user.setActif(true);
        user.setActivationToken(null);
        userRepository.save(user);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request.nom() == null || request.prenom() == null ||
            request.email() == null || request.password() == null) {
            throw new IllegalArgumentException("Tous les champs sont obligatoires");
        }
        validatePasswordPolicy(request.password());
        AddressRequest adresse = request.adresse();
        if (adresse == null
            || isBlank(adresse.numero()) || isBlank(adresse.rue())
            || isBlank(adresse.ville()) || isBlank(adresse.codePostal())
            || isBlank(adresse.pays())
            || adresse.latitude() == null || adresse.longitude() == null) {
            throw new IllegalArgumentException("L'adresse est obligatoire et doit être géolocalisée");
        }
    }

    private void validatePasswordPolicy(String password) {
        if (password.length() < 12
            || !password.matches(".*[A-Z].*")
            || !password.matches(".*[a-z].*")
            || !password.matches(".*\\d.*")
            || !password.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException(
                "Le mot de passe doit contenir au moins 12 caractères, dont une majuscule, "
                + "une minuscule, un chiffre et un caractère spécial."
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void attachTeams(User user, List<Long> teamIds) {
        if (teamIds == null || teamIds.isEmpty()) {
            return;
        }
        Set<Team> teams = new HashSet<>(teamRepository.findAllById(teamIds));
        user.setEquipesSupporte(teams);
    }

    private Address buildAddress(AddressRequest dto, User user) {
        Address address = new Address();
        address.setNumero(dto.numero());
        address.setRue(dto.rue());
        address.setVille(dto.ville());
        address.setCodePostal(dto.codePostal());
        address.setPays(dto.pays());
        address.setLatitude(dto.latitude());
        address.setLongitude(dto.longitude());
        address.setUtilisateur(user);
        return address;
    }

    private AuthResponse generateAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(user.getId(), token, user.getEmail(), user.getNom(), user.getPrenom());
    }
}
