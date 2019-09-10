package com.abnamro.care.event.in.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.abnamro.care.event.model.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendEventServiceImpl implements SendEventService {

	@Value("${spring.cloud.stream.bindings.output.destination}")
	private String sendTopic;

	private final KafkaTemplate<String, Event> kafkaTemplate;

	public void sendEventService(Event event, String careId) {
		Message<Event> message = MessageBuilder.withPayload(event)
				 .setHeader(KafkaHeaders.TOPIC, sendTopic)
				.setHeader(KafkaHeaders.MESSAGE_KEY, careId)
				// .setHeader(KafkaHeaders.PARTITION_ID, 0)
				.setHeader("X-Publisher-Id", event.getPublisherId()).build();

		log.info("sending message='{}' to topic='{}' with key='{}'", event, new String(), careId);
		kafkaTemplate.send(message);
		

	}

}
