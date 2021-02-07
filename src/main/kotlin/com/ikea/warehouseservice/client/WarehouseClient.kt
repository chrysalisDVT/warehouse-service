package com.ikea.warehouseservice.client

import com.ikea.warehouseservice.exceptions.EXCEPTION_CODE
import com.ikea.warehouseservice.exceptions.WarehouseClientException
import com.ikea.warehouseservice.vo.OrderErrorResponseVO
import com.ikea.warehouseservice.vo.WarehouseVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import reactor.core.publisher.Mono


@Component
class WarehouseClient @Autowired constructor(
    var loadBalancedWebClient: WebClient,
    @Value("\${product.service}") val productService: String
) {
    fun cascadeArticleUpdateForProd(artIds: MutableList<String>): Mono<Void> {
        loadBalancedWebClient.post()

        return loadBalancedWebClient
            .post()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(WarehouseVO(artIds = artIds))
            .retrieve()
            .onStatus({ obj: HttpStatus -> obj.isError }
            ) { clientResponse ->
                clientResponse
                    .bodyToMono(OrderErrorResponseVO::class.java)
                    .flatMap { errorResponse ->
                        Mono.error(
                            WarehouseClientException(
                                EXCEPTION_CODE.INTERNAL_ERROR,
                                errorResponse.errorMessage
                            )
                        )
                    }
            }
            .bodyToMono(Void::class.java)
    }
}