package com.northwilds.drafts

import com.bifrostorbit.cms.CmsWebhook
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest
import kotlin.system.exitProcess

private val readable = setOf("resolve", "content", "navigation", "global", "list")

private fun required(name: String): String {
    val value = System.getenv(name)
    if (value.isNullOrBlank()) {
        System.err.println("$name is not set. Add it to the drafts server's environment and start it again.")
        exitProcess(1)
    }
    return value
}

fun main() {
    val cmsApi = required("CMS_API_URL").trimEnd('/') + "/api/cdn/v1"
    val secretKey = required("CMS_PREVIEW_KEY")
    val draftsToken = required("DRAFTS_TOKEN").toByteArray()
    val webhookSecret = System.getenv("CMS_WEBHOOK_SECRET")?.takeIf { it.isNotBlank() }
    if (webhookSecret == null) println("CMS_WEBHOOK_SECRET is not set, so /cms-webhook answers 503 until you set it and restart.")
    val http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()

    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {
        routing {
            post("/cms-webhook") {
                if (webhookSecret == null) return@post call.respond(HttpStatusCode.ServiceUnavailable)
                val message = CmsWebhook.parse(
                    webhookSecret,
                    call.request.headers[CmsWebhook.TIMESTAMP_HEADER].orEmpty(),
                    call.request.headers[CmsWebhook.SIGNATURE_HEADER].orEmpty(),
                    call.receive<ByteArray>(),
                ) ?: return@post call.respond(HttpStatusCode.Unauthorized)
                call.respond(HttpStatusCode.NoContent)
                if (message.event != "webhook.test") sendRefreshMessage()
            }
            get("/cms/api/cdn/v1/{endpoint}") {
                val token = call.request.headers["X-Drafts-Token"].orEmpty().toByteArray()
                if (!MessageDigest.isEqual(token, draftsToken)) return@get call.respond(HttpStatusCode.Unauthorized)
                val endpoint = call.parameters["endpoint"]
                if (endpoint !in readable) return@get call.respond(HttpStatusCode.NotFound)

                val query = call.request.uri.substringAfter('?', "")
                val target = "$cmsApi/$endpoint?" + listOf(query, "draft=true").filter { it.isNotEmpty() }.joinToString("&")
                val request = HttpRequest.newBuilder(URI(target))
                    .header("Authorization", "Bearer $secretKey")
                    .header("Accept", "application/json")
                    .build()
                val answer = http.send(request, HttpResponse.BodyHandlers.ofString())
                call.respondText(answer.body(), ContentType.Application.Json, HttpStatusCode.fromValue(answer.statusCode()))
            }
        }
    }.start(wait = true)
}

private fun sendRefreshMessage() {}
