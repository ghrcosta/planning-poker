package com.ghrcosta.planningpoker

import com.ghrcosta.planningpoker.dependency.StorageHandler
import com.ghrcosta.planningpoker.service.RoomService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class PlanningPokerApplication {
	@Bean
	fun createRoomService(storageHandler: StorageHandler): RoomService {
		return RoomService(storageHandler)
	}
}

fun main(args: Array<String>) {
	runApplication<PlanningPokerApplication>(*args)
}