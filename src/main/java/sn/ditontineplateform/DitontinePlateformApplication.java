package sn.ditontineplateform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Configuration
public class DitontinePlateformApplication {

    public static void main (String[] args) {
        SpringApplication.run(DitontinePlateformApplication.class, args);
    }

}
