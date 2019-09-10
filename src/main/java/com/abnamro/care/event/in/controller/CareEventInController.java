package com.abnamro.care.event.in.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abnamro.care.event.in.service.SendEventService;
import com.abnamro.care.event.in.util.DomainConstants;
import com.abnamro.care.event.in.util.UUIDGenerator;
import com.abnamro.care.event.model.Event;
import com.abnamro.care.event.model.EventResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paymentarreareventpublish/v1")
public class CareEventInController {

	private final SendEventService sendEventService;

	@PostMapping
	public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody Event event)
			throws UnsupportedEncodingException {
		
		EventResponse response = new EventResponse();		
		response.setEventId(UUIDGenerator.generateType5UUID(DomainConstants.NAMESPACE_URL,
				String.valueOf(event.getAgreementId()) + String.valueOf(event.getCustomerId()) + LocalDateTime.now().toString()).toString());
		
		sendEventService.sendEventService(event, response.getEventId());
		
		return new ResponseEntity<EventResponse>(response, HttpStatus.OK);
	}

}
