package insurenceMain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info=@Info(title="PdF Generate Send Email", version="1.0",description="Saneer" ))
public class MicroServicesCreatePlan7Application {

	public static void main(String[] args) {
		SpringApplication.run(MicroServicesCreatePlan7Application.class, args);
	}

}
