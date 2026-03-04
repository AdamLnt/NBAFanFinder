package com.NBAFanFinder.Backend.Services;

import com.NBAFanFinder.Backend.DTOs.Users.AllUsersResponse;
import com.NBAFanFinder.Backend.DTOs.Users.UserResponse;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    
    public List<AllUsersResponse> findAll() {
        List<User> users = userRepository.findAll();
        List<AllUsersResponse> response = users.stream().map(AllUsersResponse::from).toList();
        return response;
    }

    public UserResponse findById(Long id) {
        return userRepository.findById(id)
            .map(UserResponse::from)
            .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé : " + id));

    }

}
