package com.ikea.warehouseservice

import com.ikea.warehouseservice.controller.WarehouseController
import com.ikea.warehouseservice.entities.WarehouseEntity
import com.ikea.warehouseservice.repository.WarehouseRepository
import com.ikea.warehouseservice.service.WarehouseService
import com.ikea.warehouseservice.vo.WarehouseVO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient


@WebFluxTest(controllers = [WarehouseController::class])
@PropertySource("classpath:warehouse-service-test.properties")
class WarehouseServiceApplicationTests {
	@MockBean
	lateinit var warehouseRepository: WarehouseRepository

	@MockBean
	 val warehouseService: WarehouseService? = null

	@Autowired
	 val webTestClient: WebTestClient? = null

	@Autowired
	lateinit var controller: WarehouseController


	@BeforeEach
	fun setup() {
		val warehouseEntity = WarehouseEntity("12", "Screw", 12)
		var testClient = WebTestClient.bindToController(controller).build()
		warehouseRepository.save(warehouseEntity)
	}

	@Test
	fun `get all inventory from warehouse`() {
		val warehouseEntity = WarehouseEntity("12", "Screw", 12)
		val testClient = WebTestClient.bindToController(controller).build()
		val warehouseVO:WarehouseVO=WarehouseVO(warehouseInventory = listOf(warehouseEntity).toMutableList())
		val data = testClient.get().uri("/warehouse")
				.header(HttpHeaders.ACCEPT, "application/json")
				.exchange()
				.expectStatus().isOk
			assertAll({ data.expectBody(WarehouseVO::class.java)},
					{ data.expectStatus().is2xxSuccessful},
			)
	}
}




