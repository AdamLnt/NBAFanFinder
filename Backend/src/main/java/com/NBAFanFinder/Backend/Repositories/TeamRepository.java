package com.NBAFanFinder.Backend.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.NBAFanFinder.Backend.Entities.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByOrderByVilleAsc();

    boolean existsByNom(String nom);
}
