package com.ikea.warehouseservice.repository

import com.ikea.warehouseservice.entities.WarehouseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * REpository layer for warehooouse inventory
 */
@Repository
interface WarehouseRepository:JpaRepository<WarehouseEntity, String>{
    fun deleteByArtId(artId: String)


    @Modifying
    @Query("delete from WarehouseEntity wh where wh.artId IN ?1")
    fun deleteArticleByArtIds(artIds: List<String>)

    @Modifying
    @Query("update WarehouseEntity wh  set wh.stock= wh.stock-1 where wh.artId IN ?1")
    fun updateArticleByArtIds(artIds: List<String>)

    @Modifying
    @Query("update WarehouseEntity wh  set wh.stock=:stock where wh.artId = :artId")
    fun updateArticleByArticle(artId:String,stock:Int)

    @Query("select wh from WarehouseEntity wh where wh.artId IN ?1")
    fun findByArticleByArtIds(artIds: MutableSet<String?>):MutableList<WarehouseEntity>
}
