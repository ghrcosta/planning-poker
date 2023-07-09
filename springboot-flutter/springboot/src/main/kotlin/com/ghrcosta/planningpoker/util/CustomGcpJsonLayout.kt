package com.ghrcosta.planningpoker.util

import ch.qos.logback.classic.spi.ILoggingEvent
import com.google.cloud.spring.logging.StackdriverJsonLayout

/**
 * This is an attempt to add extra data to logs sent to Cloud Logging.
 * See logback-spring.xml resource file as well as the following links:
 * - https://googlecloudplatform.github.io/spring-cloud-gcp/reference/html/index.html#cloud-logging
 * - https://cloud.google.com/logging/docs/agent/logging/configuration#process-payload
 * - https://cloud.google.com/logging/docs/reference/v2/rest/v2/LogEntry
 * - https://cloud.google.com/docs/samples?language=java&product=cloudlogging
 */
class CustomGcpJsonLayout : StackdriverJsonLayout() {

    override fun toJsonMap(event: ILoggingEvent): Map<String, Any> {
        val map = super.toJsonMap(event)

        // https://cloud.google.com/logging/docs/agent/logging/configuration#process-payload
        // https://cloud.google.com/logging/docs/reference/v2/rest/v2/LogEntry
        // https://cloud.google.com/logging/docs/reference/v2/rest/v2/LogEntry#LogEntrySourceLocation
        val json = "{\"file\":\"${event.loggerName}\"}"
        add(SOURCE_LOCATION, true, json, map)

        return map
    }

    companion object {
        private const val SOURCE_LOCATION = "logging.googleapis.com/sourceLocation"
    }
}