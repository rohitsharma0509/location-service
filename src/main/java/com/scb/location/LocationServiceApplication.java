package com.scb.location;

import com.scb.rider.tracing.tracer.EnableBasicTracer;
import com.scb.rider.tracing.tracer.logrequest.EnableRequestLog;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
@EnableRequestLog
@EnableBasicTracer
public class LocationServiceApplication {

	@Value("${kafka.topic}")
	private String topic;

	@Value("${kafka.noOfPartitions}")
	private int noOfPartitions;

	public static void main(String[] args) {
		SpringApplication.run(LocationServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplateBuilder()
				.setConnectTimeout(Duration.ofMillis(3000))
				.setReadTimeout(Duration.ofMillis(3000))
				.build();
	}

	@Bean
	public NewTopic topicExample() {
		return TopicBuilder.name(topic)
				.partitions(noOfPartitions)
				.replicas(3)
				.build();
	}
}
