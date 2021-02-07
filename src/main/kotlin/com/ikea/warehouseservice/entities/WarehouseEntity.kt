package com.ikea.warehouseservice.entities

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class WarehouseEntity(
    @JsonProperty("art_id")@Id var artId: String,
    var name: String?=null,
    @JsonProperty("stock")
    @JsonAlias("amount_of")
    var stock: Int?=null

)