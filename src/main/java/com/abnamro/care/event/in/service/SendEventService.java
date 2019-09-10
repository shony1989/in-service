package com.abnamro.care.event.in.service;

import com.abnamro.care.event.model.Event;

public interface SendEventService {
	
	void sendEventService(Event event, String careId);

}
