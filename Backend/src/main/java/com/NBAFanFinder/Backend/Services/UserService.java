package com.NBAFanFinder.Backend.Services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.NBAFanFinder.Backend.DTOs.Teams.TeamResponse;
import com.NBAFanFinder.Backend.DTOs.Users.AllUsersResponse;
import com.NBAFanFinder.Backend.DTOs.Users.MapUserResponse;
import com.NBAFanFinder.Backend.DTOs.Users.UserLocationResponse;
import com.NBAFanFinder.Backend.DTOs.Users.UserResponse;
import com.NBAFanFinder.Backend.Entities.Address;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Repositories.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<AllUsersResponse> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(AllUsersResponse::from).toList();
    }

    public UserResponse findById(Long id) {
        return userRepository.findById(id)
            .map(UserResponse::from)
            .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé : " + id));

    }

    @Transactional(readOnly = true)
    public UserLocationResponse findMyLocation(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé : " + email));
        return user.getAdresses().stream()
            .findFirst()
            .map(addr -> new UserLocationResponse(addr.getLatitude(), addr.getLongitude(), addr.getVille()))
            .orElseThrow(() -> new NotFoundException("Aucune adresse pour l'utilisateur"));
    }

    @Transactional(readOnly = true)
    public List<MapUserResponse> findForMap(Long teamId) {
        return userRepository.findAll().stream()
            .filter(User::getActif)
            .filter(user -> teamId == null || hasTeam(user, teamId))
            .flatMap(user -> user.getAdresses().stream()
                .findFirst()
                .map(address -> toMapUser(user, address))
                .stream())
            .toList();
    }

    private boolean hasTeam(User user, Long teamId) {
        return user.getEquipesSupporte().stream()
            .anyMatch(team -> team.getId().equals(teamId));
    }

    private MapUserResponse toMapUser(User user, Address address) {
        List<TeamResponse> teams = user.getEquipesSupporte().stream()
            .map(TeamResponse::from)
            .toList();
        return new MapUserResponse(
            user.getId(),
            user.getNom(),
            user.getPrenom(),
            address.getVille(),
            address.getLatitude(),
            address.getLongitude(),
            teams
        );
    }
}
