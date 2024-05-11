package com.wha.warehousemanagement.exceptions;

import com.wha.warehousemanagement.models.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseObject> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("400", e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("400", e.getMessage(), null));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseObject> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("500", "Null value encountered in the application", null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseObject> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("400", e.getMessage(), null));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseObject> handleCustomException(CustomException e) {
        return ResponseEntity.status(e.getStatus())
                .body(new ResponseObject("error", e.getMessage(), null));
    }

}
