/*
package priv.yanyang.webim.controller;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;

@ControllerAdvice
public class ExceptionCapture {

    Logger logger = Logger.getLogger(ExceptionCapture.class);


    @ExceptionHandler
    public void all(Exception e, HttpServletRequest request, HttpServletResponse response){
        if(e instanceof ClientAbortException){
            logger.warn("client abort connection.");
        }else{
            logger.error(e.getMessage(),e);
            String forwardUrl = "/warn?msg=unknown error&httpStatus="+ HttpStatus.UNAUTHORIZED;
            try {
                request.getRequestDispatcher(forwardUrl).forward(request,response);
            } catch (ServletException e1) {
                logger.error(e1.getMessage(),e);
            } catch (IOException e1) {
                logger.error(e1.getMessage(),e);
            }
        }
    }

}
*/
