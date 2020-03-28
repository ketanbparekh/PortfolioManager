package com.fintech.errordetails;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fintech.exception.FintechException;

@ControllerAdvice
public class FintechExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public final ResponseEntity<ErrorDetails> handleNumberFormatException(Exception ex, WebRequest wr) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Input should be a numeric integer value ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest vl) {

        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Invalid Input ", ex.getMessage());
//        if (ex instanceof FintechException) {
//            errorDetails.setDetails("Input should be not be empty ");
//        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }
}
