package com.musala.drones;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DBConfiguration {

    @Bean
    public DataSource inMemoryDS() throws Exception {
        return EmbeddedPostgres.builder()
                .start().getPostgresDatabase();
    }
}
