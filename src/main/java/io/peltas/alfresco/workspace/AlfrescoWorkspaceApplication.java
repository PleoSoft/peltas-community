package io.peltas.alfresco.workspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlfrescoWorkspaceApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(AlfrescoWorkspaceApplication.class, args);
	}
}
