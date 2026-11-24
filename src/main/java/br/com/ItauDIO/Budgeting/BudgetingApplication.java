package br.com.ItauDIO.Budgeting;

import br.com.ItauDIO.Budgeting.application.ListTransactionsByCategoryUseCase;
import br.com.ItauDIO.Budgeting.application.PersistTransactionUseCase;
import br.com.ItauDIO.Budgeting.application.SumTransactionsByCategoryUseCase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.Charset;

@SpringBootApplication
public class BudgetingApplication {

	@Bean
	ChatClient chatClient(ChatClient.Builder builder,
						  @Value("classpath:/prompts/system-message.st") Resource systemPrompt,
						  PersistTransactionUseCase persistTransactionUseCase,
						  ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
						  SumTransactionsByCategoryUseCase sumTransactionsByCategoryUseCase) throws IOException {
		return builder
				.defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
				.defaultTools(persistTransactionUseCase, listTransactionsByCategoryUseCase, sumTransactionsByCategoryUseCase)
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(BudgetingApplication.class, args);
	}

}
