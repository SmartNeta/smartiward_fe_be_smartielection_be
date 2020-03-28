package com.mnt.sampark.core.controlleradvice;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvise {
	
	@ExceptionHandler(AuthorizationException.class)
	public String noauthentication(){
		return "/open/unauthorized";
	}

}
