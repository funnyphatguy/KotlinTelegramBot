package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val BOT_URL = "https://api.telegram.org/bot"

class TelegramBotService(val botToken: String) {

    private val client: HttpClient = HttpClient.newBuilder().build()

    fun sendMessage(chatId: String?, messageText: String): String {
        val urlSendMessage: String = "$BOT_URL$botToken/sendMessage?text=$messageText&chat_id=$chatId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun getUpdates(updateId: Int): String {
        val urlGetMessage: String = "$BOT_URL$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetMessage)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMenu(chatId: String?): String {
        val urlSendMessage: String = "$BOT_URL$botToken/sendMessage"
        val sendMenuBody = """
    {
        "chat_id": "$chatId",
        "text": "Основное меню",
        "reply_markup": {
            "inline_keyboard": [
                [
                    {
                        "text": "Изучать слова",
                        "callback_data": "data1"
                    }
                ],
                [
                    {
                        "text": "Статистика",
                        "callback_data": "data2"
                    }
                ]
            ]
        }
    }
""".trimIndent()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}