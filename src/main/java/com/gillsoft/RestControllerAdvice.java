package com.gillsoft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gillsoft.model.RestError;

@ControllerAdvice
@RestController
public class RestControllerAdvice {
	
	private static Logger LOGGER = LoggerFactory.getLogger(RestControllerAdvice.class.getName());
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RestError allExceptions(Exception e) {
		LOGGER.info(e.getMessage(), e);
		return new RestError(e.getClass().getName(), e.getMessage());
	}

}
