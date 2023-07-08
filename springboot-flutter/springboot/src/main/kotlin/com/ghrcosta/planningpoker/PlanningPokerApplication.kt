package com.ghrcosta.planningpoker

import com.ghrcosta.planningpoker.dependency.StorageHandler
import com.ghrcosta.planningpoker.service.RoomService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@SpringBootApplication
class PlanningPokerApplication {
	@Bean
	fun createRoomService(storageHandler: StorageHandler): RoomService {
		return RoomService(storageHandler)
	}
}

/**
 * By default, only browser requests coming from the same address ("origin") as the server are allowed. This
 * configuration changes it so all addresses are accepted. This is required for development, as the Spring Boot server
 * runs on port 8080 and Flutter's debug server is in a different port. The conditional property ensures this is not
 * enabled on production.
 */
@Configuration
@ConditionalOnProperty(name= ["allow-cross-origin"], havingValue="true")
class WebConfig : WebMvcConfigurer {
	private val log = LoggerFactory.getLogger(WebConfig::class.java)
	override fun addCorsMappings(registry: CorsRegistry) {
		log.info("Setting CORS allowed in all paths")
		registry.addMapping("/**").allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
	}
}

fun main(args: Array<String>) {
	val log = LoggerFactory.getLogger(PlanningPokerApplication::class.java)

	runApplication<PlanningPokerApplication>(*args) {
		// https://cloud.google.com/appengine/docs/standard/java-gen2/runtime#environment_variables
		val deployVersion = System.getenv("GAE_VERSION") ?: "local"
		val profile = if (deployVersion == "local") "local" else "prod"
		log.info("deploy=${deployVersion}, profile=${profile}")

		this.setAdditionalProfiles(profile)
	}
}