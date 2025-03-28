package dev.bast.foro.foro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"dev.bast.foro"})
@EntityScan(basePackages = {"dev.bast.foro.usuarios.model", "dev.bast.foro.foros.model"})
@EnableJpaRepositories(basePackages = {"dev.bast.foro.usuarios.repository", "dev.bast.foro.foros.repository"})
public class ForoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForoApplication.class, args);
	}

}
