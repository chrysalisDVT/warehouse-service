package com.ikea.warehouseservice.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.http.client.BufferingClientHttpRequestFactory

import org.springframework.http.client.SimpleClientHttpRequestFactory

import java.util.ArrayList

import org.springframework.http.client.ClientHttpRequestInterceptor

import org.springframework.cloud.client.loadbalancer.LoadBalanced

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.web.reactive.function.client.WebClient
/**
 * Warehouse configuration for loadbalanced web client
 */
@Configuration
class WarehouseConfiguration @Autowired constructor(var loadBalancerClient: LoadBalancerClient){

    companion object {
        val log: Logger = LoggerFactory.getLogger(WarehouseConfiguration::class.java.name)
    }

    @Bean
    @LoadBalanced
    fun builder(): WebClient.Builder? {
        return WebClient.builder()
    }

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient? {
        return builder.build()
    }
}