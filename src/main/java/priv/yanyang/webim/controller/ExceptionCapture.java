package priv.yanyang.webim.controller;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.annotation.Annotation;

@ControllerAdvice
public class ExceptionCapture {

    Logger logger = Logger.getLogger(ExceptionCapture.class);

    @ExceptionHandler
    public ResponseEntity all(Exception e){
        logger.error(e.getMessage(),e);
        return new ResponseEntity("Unknown exception", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
