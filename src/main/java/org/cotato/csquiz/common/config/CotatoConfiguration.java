package org.cotato.csquiz.common.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class CotatoConfiguration {

	@Bean("quizSendThreadPoolExecutor")
	public Executor quizSendThreadPoolExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(20);
		taskExecutor.setMaxPoolSize(100);
		taskExecutor.setQueueCapacity(10000);
		taskExecutor.setThreadNamePrefix("quiz-send-thread-");
		taskExecutor.initialize();
		return taskExecutor;
	}

	@Bean("emailSendThreadPoolExecutor")
	public Executor emailSendThreadPoolExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(7);
		taskExecutor.setMaxPoolSize(14);
		taskExecutor.setQueueCapacity(1000);
		taskExecutor.setThreadNamePrefix("email-send-thread-");
		taskExecutor.initialize();
		return taskExecutor;
	}
}
