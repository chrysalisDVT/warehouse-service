package com.ikea.warehouseservice.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.util.ResourceUtils
import java.util.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ikea.warehouseservice.entities.WarehouseEntity
import com.ikea.warehouseservice.exceptions.EXCEPTION_CODE
import com.ikea.warehouseservice.exceptions.WarehouseRepositoryException
import com.ikea.warehouseservice.repository.WarehouseRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import javax.persistence.*

/**
 * Boostrap setup for warehouse inventory
 */
@Component
class InitialWarehouseSetup @Autowired constructor(
    var warehouseRepository: WarehouseRepository,
    @Value("\${data.source.path}") var filePath: String
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(InitialWarehouseSetup::class.java.name)

    }

    @PostConstruct
    fun loadProductData() {
        val jsonFile = ResourceUtils.getFile("classpath:${filePath}")
        val mapper: ObjectMapper = jacksonObjectMapper()
        val dataString: String = mapper.readTree(jsonFile).get("inventory").toString()
        print(dataString)
        try {
            //Reads the inventory information from the source provided and persists the information for initial use
            val inventoryInformation: Array<WarehouseEntity> =
                mapper.readValue(dataString, Array<WarehouseEntity>::class.java)
            log.info(inventoryInformation.contentToString())
            warehouseRepository.saveAll(inventoryInformation.toMutableList())
            warehouseRepository.findAll().forEach { log.info(it.toString()) }
        } catch (ex: Exception) {
            throw WarehouseRepositoryException(EXCEPTION_CODE.INTERNAL_ERROR, "Error bootstrapping the application")
        }
    }
}


