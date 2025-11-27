package com.example.space;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.util.List;

@Configuration
@Profile("!test")  // Не загружается при профиле test
public class DatabaseConfig {

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer init = new ConnectionFactoryInitializer();
        init.setConnectionFactory(connectionFactory);
        init.setDatabasePopulator(new ResourceDatabasePopulator(
                new ClassPathResource("schema.sql")));
        return init;
    }

    @Bean
    public CommandLineRunner initData(MissionRepository repository) {
        return args -> {
            repository.deleteAll()
                    .thenMany(repository.saveAll(List.of(
                            Mission.builder()
                                    .name("Artemis III")
                                    .destination("Moon")
                                    .launchYear(2025)
                                    .status("PLANNED")
                                    .crewSize(4)
                                    .build(),
                            Mission.builder()
                                    .name("Mars Pioneer")
                                    .destination("Mars")
                                    .launchYear(2030)
                                    .status("PLANNED")
                                    .crewSize(6)
                                    .build(),
                            Mission.builder()
                                    .name("ISS Expedition 70")
                                    .destination("ISS")
                                    .launchYear(2024)
                                    .status("LAUNCHED")
                                    .crewSize(7)
                                    .build(),
                            Mission.builder()
                                    .name("Europa Clipper")
                                    .destination("Europa")
                                    .launchYear(2024)
                                    .status("LAUNCHED")
                                    .crewSize(0)
                                    .build(),
                            Mission.builder()
                                    .name("Apollo 11")
                                    .destination("Moon")
                                    .launchYear(1969)
                                    .status("COMPLETED")
                                    .crewSize(3)
                                    .build()
                    )))
                    .subscribe(m -> System.out.println("Created: " + m.getName()));
        };
    }
}
