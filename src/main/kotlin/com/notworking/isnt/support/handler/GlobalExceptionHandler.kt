package com.notworking.isnt.support.handler

import com.notworking.isnt.controller.dto.ErrorResponse
import com.notworking.isnt.support.exception.BusinessException
import mu.KotlinLogging
import org.hibernate.exception.ConstraintViolationException
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException


private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * @ModelAttribut 으로 binding error 발생시 BindException 발생한다.
     * ref https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args
     */
    @ExceptionHandler(BindException::class)
    protected fun handleBindException(e: BindException): ResponseEntity<ErrorResponse> {
        log.error("handleBindException", e)
        val response: ErrorResponse = ErrorResponse(
            code = "E0001", title = "BindException", message = "BindException이 발생하였습니다.",
            detailMessage = e.message
        )
        return ResponseEntity.badRequest().body(response)
    }

    /**
     * enum type 일치하지 않아 binding 못할 경우 발생
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    protected fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        log.error("handleMethodArgumentTypeMismatchException", e)
        val response: ErrorResponse =
            ErrorResponse(
                code = "E0001",
                title = "MethodArgumentTypeMismatch",
                message = "MethodArgumentTypeMismatchException이 발생하였습니다.",
                detailMessage = e.message.orEmpty()
            )
        return ResponseEntity.badRequest().body(response)
    }

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    protected fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        log.error("handleHttpRequestMethodNotSupportedException", e)
        val response: ErrorResponse =
            ErrorResponse(
                code = "E0001",
                title = "HttpRequestMethodNotSupport",
                message = "호출한 메소드를 확인해주세요.",
                detailMessage = e.message.orEmpty()
            )
        return ResponseEntity.badRequest().body(response)
    }

    /**
     * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생합
     */
    @ExceptionHandler(AccessDeniedException::class)
    protected fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ErrorResponse> {
        log.error("handleAccessDeniedException", e)
        val response: ErrorResponse =
            ErrorResponse(
                code = "E0001",
                title = "AccessDenied",
                message = "권한이 없습니다.",
                detailMessage = e.message.orEmpty()
            )
        return ResponseEntity.badRequest().body(response)
    }

    /**
     * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생합
     */
    @ExceptionHandler(ConstraintViolationException::class)
    protected fun constraintViolationException(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        log.error("constraintViolationException", e)
        val response: ErrorResponse =
            ErrorResponse(
                code = "E0001",
                title = "ConstraintViolationException",
                message = "ConstraintViolationException이 발생하였습니다.",
                detailMessage = e.message.orEmpty()
            )
        return ResponseEntity.badRequest().body(response)
    }

    /**
     * validation 예외처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun methodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        log.error("MethodArgumentNotValidException", e)
        var defaultDetailMessage: String? = "검증에러 처리시 문제가 발생하였습니다."

        if (e.bindingResult.hasErrors()) {
            defaultDetailMessage = e.bindingResult.allErrors[0].codes?.get(0)
        }

        val response: ErrorResponse =
            ErrorResponse(
                code = "E0001",
                title = "ValidException",
                message = "유효성 검증시 에러가 발생하였습니다.",
                detailMessage = defaultDetailMessage.orEmpty()
            )
        return ResponseEntity.badRequest().body(response)
    }

    /**
     * 업무처리 중 에러를 발생한 경우
     */
    @ExceptionHandler(BusinessException::class)
    protected fun businessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        log.error("businessException", e)
        val response: ErrorResponse =
            ErrorResponse(
                code = e.error.code, title = "업무처리 에러", message = e.error.message, detailMessage = e.message.orEmpty()
            )
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(Exception::class)
    protected fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Exception", e)
        val response: ErrorResponse = ErrorResponse(
            code = "E0001",
            title = "Error",
            message = "에러가 발생하였습니다.",
            detailMessage = e.message.orEmpty()
        )
        return ResponseEntity.badRequest().body(response)
    }
}