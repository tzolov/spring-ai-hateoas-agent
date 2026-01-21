package com.example.spring.ai.hateoas;

import java.util.Scanner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestClient;

/**
 * Spring AI version of the https://github.com/openknowledge/hateoas-agent
 *
 * @author Christian Tzolov
 */
@SpringBootApplication
public class AgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgentApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(ChatClient.Builder chatClientBuilder,
			@Value("${hateoas.agent.entrypoint:http://localhost:8080/}") String entryPoint,
			@Value("${classpath:/system_prompt.md}") Resource systemPrompt) {
		return args -> {

			var chatClient = chatClientBuilder // @formatter:off
				.defaultSystem(s -> s.text(systemPrompt)
					.param("ENTRY_POINT", entryPoint))
				.defaultTools(new RestTools(RestClient.create()))
				.build(); // @formatter:on

			// Start the chat loop
			System.out.println("\nI am Hateoas agent.\n");

			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					System.out.print("\n> USER: ");
					System.out.println("\n> ASSISTANT: " + chatClient.prompt(scanner.nextLine()).call().content());
				}
			}
		};
	}

}
