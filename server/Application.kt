package com.voicenotes

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun main() {
    embeddedServer(
        Netty,
        port = 5000,
        host = "0.0.0.0"
    ) {
        install(ContentNegotiation) { json() }
        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowHeader(HttpHeaders.ContentType)
            anyHost()
        }
        routing {
            get("/") { call.respondText("Server running on port 5000 🚀") }
        }
    }.start(wait = true)
}
