package com.ltev.connected.exception_handler;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ControllerAdvice
@Configuration
@EnableWebMvc
public class MainExceptionHandler {

    //@ExceptionHandler(NoHandlerFoundException.class)
    public String handleException(Exception e) {
        return "Exception";
    }
}
