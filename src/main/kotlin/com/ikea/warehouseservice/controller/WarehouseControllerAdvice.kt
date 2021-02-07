package com.ikea.warehouseservice.controller

import com.ikea.warehouseservice.exceptions.EXCEPTION_CODE
import com.ikea.warehouseservice.exceptions.WarehouseExceptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ProductControllerAdvice {
    companion object{
        val log:Logger=LoggerFactory.getLogger(ProductControllerAdvice::class.java)
    }

    @ExceptionHandler(value=[WarehouseExceptions::class])
    fun productExceptionHandler(exception:WarehouseExceptions):ResponseEntity<WarehouseServiceErrorResponse> {
        log.info(exception.errorCode.toString())

        log.error(exception.errorMessage,exception)
        val  status= when(exception.errorCode){
            EXCEPTION_CODE.INTERNAL_ERROR->HttpStatus.INTERNAL_SERVER_ERROR
            EXCEPTION_CODE.BAD_REQUEST->HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        return ResponseEntity.status(status

        ).body(WarehouseServiceErrorResponse(status, exception.errorMessage, listOf(exception.localizedMessage)))
    }
}

data class WarehouseServiceErrorResponse(val status: HttpStatus,val message:String,val  errors:List<String>)
