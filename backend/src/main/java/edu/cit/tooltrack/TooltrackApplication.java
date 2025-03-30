package edu.cit.tooltrack;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(title = "TookTrack", version = "1.0", description = "API for tooltrack project")
)
@SpringBootApplication
public class TooltrackApplication {


	public static void main(String[] args) {
		SpringApplication.run(TooltrackApplication.class, args);
	}
}
