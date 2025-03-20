package edu.cit.tooltrack;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TooltrackApplication {

	static {
		// Specify the .env file path manually
		Dotenv dotenv = Dotenv.configure()
				.directory("./backend")
				.load();

		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(TooltrackApplication.class, args);
	}
}
