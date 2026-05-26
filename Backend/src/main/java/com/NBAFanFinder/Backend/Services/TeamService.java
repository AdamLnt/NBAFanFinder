package com.NBAFanFinder.Backend.Services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.NBAFanFinder.Backend.DTOs.Teams.TeamResponse;
import com.NBAFanFinder.Backend.Repositories.TeamRepository;

@Service
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<TeamResponse> findAll() {
        return teamRepository.findAllByOrderByVilleAsc().stream()
            .map(TeamResponse::from)
            .toList();
    }
}
