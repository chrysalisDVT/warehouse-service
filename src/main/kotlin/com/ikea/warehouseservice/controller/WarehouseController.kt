package com.ikea.warehouseservice.controller

import com.ikea.warehouseservice.exceptions.EXCEPTION_CODE
import com.ikea.warehouseservice.exceptions.WarehouseControllerException
import com.ikea.warehouseservice.exceptions.WarehouseServiceException
import com.ikea.warehouseservice.service.WarehouseService
import com.ikea.warehouseservice.vo.WarehouseVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/warehouse")
class WarehouseController @Autowired constructor(
    var warehouseService: WarehouseService
) {
    @GetMapping
    fun getWarehouseInventory() =
        WarehouseVO(warehouseInventory = warehouseService.getWarehouseInformation())

    @GetMapping("{id}")
    fun getById(@PathVariable("id") id: String) {
        print(id)
    }

    @PatchMapping
    fun updateWarehouseInventory(@RequestBody warehouseVO: WarehouseVO) {
        if(!warehouseVO.warehouseInventory.isNullOrEmpty()){
            println(warehouseVO)
            warehouseService.updateInventoryByArtIds(warehouseVO ,true)
        }else{
            throw WarehouseControllerException(EXCEPTION_CODE.BAD_REQUEST,"Invalid request body")
        }
    }

    @PostMapping
    fun addWarehouseInventory(@RequestBody warehouseVO: WarehouseVO) {
        if(!warehouseVO.warehouseInventory.isNullOrEmpty()){
            println(warehouseVO)
            warehouseService.updateInventoryByArtIds(warehouseVO ,false)
        }else{
            throw WarehouseControllerException(EXCEPTION_CODE.BAD_REQUEST,"Invalid request body")
        }
    }

    @DeleteMapping("{id}")
    fun removeById(@PathVariable("id") id: String):Mono<WarehouseVO> {
        try {
            warehouseService.removeArticleFromWarehouse(id)
            return Mono.just(WarehouseVO(operationStatus = true))
        } catch (ex: WarehouseServiceException) {
            throw ex
        } catch (ex: Exception) {
            throw WarehouseControllerException(
                EXCEPTION_CODE.INTERNAL_ERROR,
                "Error occurred while processing article removal"
            )
        }
    }

}