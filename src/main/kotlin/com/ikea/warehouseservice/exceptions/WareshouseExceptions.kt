package com.ikea.warehouseservice.exceptions

import java.lang.RuntimeException

open class  WarehouseExceptions(val errorCode:EXCEPTION_CODE, val errorMessage:String):RuntimeException()

class WarehouseServiceException( errorCode: EXCEPTION_CODE,errorMessage: String,):WarehouseExceptions(errorCode,errorMessage)

class WarehouseControllerException(errorCode: EXCEPTION_CODE,errorMessage: String):WarehouseExceptions(errorCode,errorMessage)

class WarehouseClientException(errorCode: EXCEPTION_CODE, errorMessage: String):WarehouseExceptions(errorCode,errorMessage)

class WarehouseRepositoryException(errorCode: EXCEPTION_CODE,errorMessage: String):WarehouseExceptions(errorCode,errorMessage)

enum class EXCEPTION_CODE(errorCode:String){
    INTERNAL_ERROR("INTERNAL_PROCESS_ERROR"),
    BAD_REQUEST("INVALID_REQUEST"),
    UNAUTHORIZED("NOT_AUTHORIZED"),
}