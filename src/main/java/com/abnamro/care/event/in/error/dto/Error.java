package com.abnamro.care.event.in.error.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Error implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ErrorAttributes> errors;

}
