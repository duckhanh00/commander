package com.commander.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import com.commander.common.exception.BusinessErrorCode;
import com.commander.common.exception.BusinessException;
import com.commander.common.exception.FieldViolation;
import com.commander.common.exception.ValidateException;
import com.commander.common.model.response.Response;
import com.commander.common.util.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.List;

@Log4j2
@ControllerAdvice
public record ExceptionController(ObjectMapper objectMapper) {
    @ExceptionHandler(BusinessException.class)
    private void handleBusinessException(BusinessException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, e.getErrorCode(), request, response);
    }

    @ExceptionHandler(Exception.class)
    private void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, ErrorCode.INTERNAL_SERVER_ERROR, request, response);
    }

    @ExceptionHandler(BindException.class)
    private void handleBindException(BindException e, HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {
        var fieldViolations = e.getBindingResult().getAllErrors().stream()
                .map(error -> new FieldViolation(((FieldError) error).getField(), error.getDefaultMessage()))
                .toList();

        handleInvalidParams(e, fieldViolations, request, response);
    }

    @ExceptionHandler(ValidateException.class)
    private void handleValidateException(ValidateException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleInvalidParams(e, e.getFieldViolations(), request, response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private void handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, ErrorCode.INVALID_PARAMETERS, request, response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    private void handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        var fieldViolations = List.of(new FieldViolation(e.getParameterName(), e.getMessage()));
        handleInvalidParams(e, fieldViolations, request, response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private void handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        var fieldViolations = List.of(new FieldViolation(e.getName(), e.getMessage()));
        handleInvalidParams(e, fieldViolations, request, response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    private void handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, ErrorCode.FORBIDDEN, request, response);
    }

    @ExceptionHandler(AuthenticationException.class)
    private void handleAuthenticationException(AuthenticationException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, ErrorCode.UNAUTHORIZED, request, response);
    }

    private void handle(Exception e, BusinessErrorCode errorCode, HttpServletRequest request, HttpServletResponse response) throws IOException {
        var errorResponse = Response.ofFailed(errorCode, e.getMessage());
        log.error("{}", errorResponse, e);
        writeResponse(response, errorCode.httpStatus(), errorResponse);
    }

    private void handleInvalidParams(Exception e, List<FieldViolation> fieldViolations, HttpServletRequest request, HttpServletResponse response) throws IOException {
        var errorResponse = Response.ofFailed(ErrorCode.INVALID_PARAMETERS, e.getMessage(), fieldViolations);
        log.error("{}", errorResponse, e);
        writeResponse(response, ErrorCode.INVALID_PARAMETERS.httpStatus(), errorResponse);
    }

    private void writeResponse(HttpServletResponse servletResponse, int httpStatus, Response<?> errorResponse) throws IOException {
        servletResponse.setStatus(httpStatus);
        servletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        byte[] body = objectMapper.writeValueAsBytes(errorResponse);
        servletResponse.setContentLength(body.length);
        servletResponse.getOutputStream().write(body);
    }
}