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
import reactor.core.publisher.Mono
import java.util.function.Consumer
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
    fun updateInventoryByArtIds(articlesVo: WarehouseVO?, isUpdate: Boolean): Mono<WarehouseVO> {
        log.info("Updating the inventory based on the product transaction")
        try {

            //Checking if the article is available
            articlesVo?.let {
                val articleStockMap = articlesVo.warehouseInventory?.stream()?.collect(Collectors.toMap(WarehouseEntity::artId, WarehouseEntity::stock))
                //Updated implementation
                val availableArticleStock = articlesVo.warehouseInventory?.parallelStream()?.map(WarehouseEntity::artId)?.let {
                    warehouseRepository.findByArticleByArtIds(it.collect(Collectors.toSet()))
                }
                val outOfStockArticle = availableArticleStock?.parallelStream()?.filter { it.stock!! < articleStockMap?.get(it.artId)!! }?.collect(Collectors.toList())
                var inventoryConsumer: (wareHouseEntity: WarehouseEntity) -> Unit =
                        if (isUpdate) {
                        //Consumer body
                            {
                                if (outOfStockArticle.isNullOrEmpty()) {
                                    print("Test inventory deduction: $it")
                                    it.stock?.minus(articleStockMap?.get(it.artId)!!)
                                            ?.let { articleStock ->
                                                warehouseRepository.updateArticleByArticle(it.artId, if (articleStock >= 0) articleStock else 0)
                                            }
                                }
                            }
                        //Consumer body end
                        } else {
                        //Consumer body
                            {
                                print("Test inventory addition: $it")
                                it.stock?.plus(articleStockMap?.get(it.artId)!!)
                                        ?.let { articleStock ->
                                            warehouseRepository.updateArticleByArticle(it.artId, if (articleStock >= 0) articleStock else 0)
                                        }
                            }

                        //Consumer body end
                        }
                //Updating the warehouse inventory with the updated stock, if updated stock is negative the we cannot sell that product and exception is thrown
                availableArticleStock
                        ?.forEach(inventoryConsumer)
                return Mono.just(WarehouseVO(operationStatus = outOfStockArticle.isNullOrEmpty()))
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
