package edu.cit.tooltrack;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(title = "TookTrack", version = "1.0", description = "API for tooltrack project")
)
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
