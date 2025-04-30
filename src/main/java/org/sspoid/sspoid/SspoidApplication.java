package org.sspoid.sspoid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SspoidApplication {

	public static void main(String[] args) {
		SpringApplication.run(SspoidApplication.class, args);
	}

}
