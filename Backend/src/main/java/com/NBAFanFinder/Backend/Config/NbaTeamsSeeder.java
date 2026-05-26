package com.NBAFanFinder.Backend.Config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.NBAFanFinder.Backend.Entities.Team;
import com.NBAFanFinder.Backend.Repositories.TeamRepository;

@Configuration
public class NbaTeamsSeeder implements CommandLineRunner {

    private static final List<String[]> NBA_TEAMS = List.of(
        new String[] {"Atlanta Hawks", "Atlanta"},
        new String[] {"Boston Celtics", "Boston"},
        new String[] {"Brooklyn Nets", "Brooklyn"},
        new String[] {"Charlotte Hornets", "Charlotte"},
        new String[] {"Chicago Bulls", "Chicago"},
        new String[] {"Cleveland Cavaliers", "Cleveland"},
        new String[] {"Dallas Mavericks", "Dallas"},
        new String[] {"Denver Nuggets", "Denver"},
        new String[] {"Detroit Pistons", "Detroit"},
        new String[] {"Golden State Warriors", "San Francisco"},
        new String[] {"Houston Rockets", "Houston"},
        new String[] {"Indiana Pacers", "Indianapolis"},
        new String[] {"Los Angeles Clippers", "Los Angeles"},
        new String[] {"Los Angeles Lakers", "Los Angeles"},
        new String[] {"Memphis Grizzlies", "Memphis"},
        new String[] {"Miami Heat", "Miami"},
        new String[] {"Milwaukee Bucks", "Milwaukee"},
        new String[] {"Minnesota Timberwolves", "Minneapolis"},
        new String[] {"New Orleans Pelicans", "New Orleans"},
        new String[] {"New York Knicks", "New York"},
        new String[] {"Oklahoma City Thunder", "Oklahoma City"},
        new String[] {"Orlando Magic", "Orlando"},
        new String[] {"Philadelphia 76ers", "Philadelphia"},
        new String[] {"Phoenix Suns", "Phoenix"},
        new String[] {"Portland Trail Blazers", "Portland"},
        new String[] {"Sacramento Kings", "Sacramento"},
        new String[] {"San Antonio Spurs", "San Antonio"},
        new String[] {"Toronto Raptors", "Toronto"},
        new String[] {"Utah Jazz", "Salt Lake City"},
        new String[] {"Washington Wizards", "Washington"}
    );

    private final TeamRepository teamRepository;

    public NbaTeamsSeeder(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public void run(String... args) {
        for (String[] data : NBA_TEAMS) {
            String nom = data[0];
            String ville = data[1];
            if (!teamRepository.existsByNom(nom)) {
                teamRepository.save(new Team(nom, ville));
            }
        }
    }
}
