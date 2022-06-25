package com.wt2024.points.restful.backend.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseResult> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn(ex.getMessage(), ex);
        ObjectError objectError = ex.getBindingResult().getFieldError();
        if (Objects.isNull(objectError)) {
            objectError = ex.getAllErrors().stream().findFirst().orElseGet(null);
        }
        if (Objects.isNull(objectError)) {
            return ResponseEntity.ok(ResponseResult.builder().build().fail(PointsCode.TRANS_0047, ""));
        }
        return ResponseEntity.ok(ResponseResult.builder().build().fail(PointsCode.TRANS_0047, objectError.getDefaultMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseResult> handleValidationException(ValidationException ex) {
        log.warn(ex.getMessage(), ex);
        Throwable cause = ex.getCause();
        if (cause instanceof PointsException) {
            return ResponseEntity.ok(ResponseResult.builder().build().fail(((PointsException) cause).getPointsCode()));
        } else if (ex instanceof ConstraintViolationException) {
            return this.getResponseResultResponseEntity((ConstraintViolationException) ex);
        } else if (ex instanceof ValidationException && cause instanceof ConstraintViolationException) {
            return this.getResponseResultResponseEntity((ConstraintViolationException) cause);
        } else {
            String message = ex.getMessage();
            return ResponseEntity.ok(ResponseResult.builder().build().fail(PointsCode.TRANS_0047, message.substring(message.lastIndexOf(":"))));
        }
    }

    private ResponseEntity<ResponseResult> getResponseResultResponseEntity(ConstraintViolationException exception) {
        String field = "";
        String message = "";
        Iterator<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations().iterator();
        while (constraintViolations.hasNext()) {
            ConstraintViolation constraintViolation = constraintViolations.next();

            if (Objects.nonNull(constraintViolation)) {
                message = constraintViolation.getMessage();
                Iterator iterator = constraintViolation.getPropertyPath().iterator();
                while (iterator.hasNext()) {
                    field = iterator.next().toString();
                }
                if (field.length() > 0) {
                    field = "[" + field + "] ";
                }
            }

            if (field.indexOf("arg") < 0) {
                break;
            }
        }
        return ResponseEntity.ok(ResponseResult.builder().build().fail(PointsCode.TRANS_0047, field + message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseResult> handleBindingException(BindException ex) {
        log.warn(ex.getBindingResult().getFieldError().getDefaultMessage(), ex);
        return ResponseEntity.ok(ResponseResult.builder().build().fail(PointsCode.TRANS_0047, ex.getBindingResult().getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(PointsException.class)
    public ResponseEntity<ResponseResult> pointsExceptionHandler(PointsException ex) {
        return ResponseEntity.ok(ResponseResult.builder().build().fail(ex.getPointsCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException exception = (InvalidFormatException) cause;
            List<JsonMappingException.Reference> paths = exception.getPath();
            String field = "";
            String message = exception.getOriginalMessage();
            for (JsonMappingException.Reference reference : paths) {
                field += reference.getFieldName() + ".";
            }
            if (field.length() > 0) {
                field = "[" + field.substring(0, field.length() - 1) + "] ";
            }
            return ResponseEntity.ok(ResponseResult.builder().build().fail(PointsCode.TRANS_0047, field + message.substring(message.lastIndexOf(":"))));
        }
        return ResponseEntity.ok(ResponseResult.builder().build().fail(PointsCode.TRANS_0005));
    }
}
