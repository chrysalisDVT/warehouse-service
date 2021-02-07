package com.ikea.warehouseservice.vo

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ikea.warehouseservice.entities.WarehouseEntity

@JsonInclude(JsonInclude.Include.NON_NULL)
data class WarehouseVO(
        var artId:String?=null,
        var name:String?=null,
        var stock: Int? =null,
        @JsonProperty("articles")var warehouseInventory:MutableList<WarehouseEntity>?=null,
        var artIds:MutableList<String>?=null,
        var operationStatus:Boolean?=null
)

data class OrderErrorResponseVO(
    var errorCode:String,
    var errorMessage:String
)

