package com.abnamro.care.event.in.error.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <ul>
 * <li>code - A unique and meaningful code for the error. It is mandatory.</li>
 * <li>message - This contains a human readable description for the code and is
 * specifically meant for debug purposes.It is also mandatory.</li>
 * <li>traceId - The unique id sent in the request header for end to end
 * traceability of an API call especially in case of errors. It is not mandatory
 * but should be present.</li>
 * <li>status - The HTTP response status code under which the code has been
 * categorized. It is mandatory.</li>
 * <li>params - This contains dynamic values which need to be returned sometimes
 * by the API in addition to the code</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorAttributes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
	private String traceId;
	private int status;
	private List<String> params;
}