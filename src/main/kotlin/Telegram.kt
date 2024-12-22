package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"
    val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates"


    val client: HttpClient = HttpClient.newBuilder().build()

    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val update: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()

    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
    val updateResponse: HttpResponse<String> = client.send(update, HttpResponse.BodyHandlers.ofString())

    println(response.body())
    println(updateResponse.body())
}