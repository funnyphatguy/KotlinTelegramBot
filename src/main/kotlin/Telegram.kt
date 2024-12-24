package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        val updateIdRegex: Regex = """"update_id":(\d+)(?=,)""".toRegex()
        val updateIdMatchResult: MatchResult? = updateIdRegex.find(updates)
        if (updateIdMatchResult != null) {
            val updateIdString = updateIdMatchResult.groupValues[1]
            println("update_id: $updateIdString")
            updateId = updateIdString.toInt() + 1
        }

        val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult: MatchResult? = messageRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value

        if (text != null)
            println("Текст сообщения: $text")
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}
