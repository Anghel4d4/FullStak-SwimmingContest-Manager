package rest.Start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "rest")
@EnableJpaRepositories(basePackages = "rest.Persistence.Interface")
@EntityScan(basePackages = "rest.Model")
public class StartApp {
    public static void main(String[] args) {
        SpringApplication.run(StartApp.class, args);
    }
}