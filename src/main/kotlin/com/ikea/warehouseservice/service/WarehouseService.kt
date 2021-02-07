package com.ikea.warehouseservice.service

import com.ikea.warehouseservice.entities.WarehouseEntity
import com.ikea.warehouseservice.exceptions.EXCEPTION_CODE
import com.ikea.warehouseservice.exceptions.WarehouseRepositoryException
import com.ikea.warehouseservice.exceptions.WarehouseServiceException
import com.ikea.warehouseservice.repository.WarehouseRepository
import com.ikea.warehouseservice.vo.WarehouseVO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.NonTransientDataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

/**
 * @author Dharmvir Tiwari
 * Service layer for processing the inventory tasks
 */
@Service
class WarehouseService @Autowired constructor(
        var warehouseRepository: WarehouseRepository
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(WarehouseService::class.java)
    }

    fun getWarehouseInformation(): MutableList<WarehouseEntity> = try {
        warehouseRepository.findAll()
    } catch (ex: Exception) {
        throw WarehouseServiceException(EXCEPTION_CODE.INTERNAL_ERROR, "Exception occurred while retrieving inventory details.")
    }

    /**
     * Removes the article from the warehouse inventory
     */
    @Transactional
    fun removeArticleFromWarehouse(artId: String) {
        log.info("Removing the article from warehouse inventory")
        try {
            warehouseRepository.deleteByArtId(artId)
        } catch (ex: NonTransientDataAccessException) {
            throw WarehouseServiceException(EXCEPTION_CODE.BAD_REQUEST, "Exception occurred while updating inventory")
        } catch (e: Exception) {
            throw WarehouseServiceException(EXCEPTION_CODE.INTERNAL_ERROR, "Exception occurred while updating inventory")

        }
    }

    /**
     * Update the inventory with the latest stock details based on the product exchange
     */
    @Transactional
    fun updateInventoryByArtIds(articlesVo: WarehouseVO?, isUpdate: Boolean) {
        log.info("Updating the inventory based on the product transaction")
        //Checking if the article is available
        try {
            articlesVo?.let {
                val articleStockMap = articlesVo.warehouseInventory?.stream()?.collect(Collectors.toMap(WarehouseEntity::artId, WarehouseEntity::stock))
                var warehouseDetails: MutableList<WarehouseEntity>? = articleStockMap?.let { articles -> warehouseRepository.findByArticleByArtIds(articles.keys) }
                //Updating the warehouse inventory with the updated stock, if updated stock is negative the we cannot sell that product and exception is thrown
                warehouseDetails
                        ?.forEach { inventory ->
                            when (isUpdate) {
                                true -> inventory.stock?.minus(articleStockMap?.get(inventory.artId)!!)
                                        ?.let { articleStock ->
                                            warehouseRepository.updateArticleByArticle(inventory.artId, if (articleStock > 0) articleStock else 0)
                                        }
                                false -> inventory.stock?.plus(articleStockMap?.get(inventory.artId)!!)
                                        ?.let { articleStock ->
                                            warehouseRepository.updateArticleByArticle(inventory.artId, if (articleStock > 0) articleStock else 0)
                                        }
                            }

                        }
            } ?: run {
                throw WarehouseServiceException(EXCEPTION_CODE.BAD_REQUEST, "Exception occurred while updating inventory")
            }
        } catch (ex: NonTransientDataAccessException) {
            throw WarehouseRepositoryException(EXCEPTION_CODE.INTERNAL_ERROR, "Exception occurred while updating inventory")
        } catch (ex: Exception) {
            throw WarehouseServiceException(EXCEPTION_CODE.INTERNAL_ERROR, "Exception occurred while updating inventory")
        }
    }
}