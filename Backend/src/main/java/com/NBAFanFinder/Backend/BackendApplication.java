package com.NBAFanFinder.Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

// MongoDB est dans les deps en vue de l'utiliser pour les logs.
// En attendant, on desactive ses auto-configurations pour ne pas tenter
// une connexion localhost:27017 au boot (timeout 30s+).
// Pour activer Mongo : retirer les 3 classes Mongo de la liste exclude
// et configurer spring.data.mongodb.uri.
@SpringBootApplication(exclude = {
	UserDetailsServiceAutoConfiguration.class,
	MongoAutoConfiguration.class,
	DataMongoAutoConfiguration.class,
	DataMongoRepositoriesAutoConfiguration.class
})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
