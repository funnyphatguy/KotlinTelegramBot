package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val BOT_URL = "https://api.telegram.org/bot"
const val LEARN_WORDS_RESPONSE = "learn_words_clicked"
const val STATISTICS_RESPONSE = "statistics_clicked"

class TelegramBotService(val botToken: String) {

    private val client: HttpClient = HttpClient.newBuilder().build()

    fun sendMessage(chatId: String?, messageText: String): String {
        val encoded = URLEncoder.encode(
            messageText,
            StandardCharsets.UTF_8
        )
        println(encoded)

        val urlSendMessage: String = "$BOT_URL$botToken/sendMessage?text=$encoded&chat_id=$chatId"
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

    fun sendMenu(chatId: String): String {
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
                        "callback_data": "$LEARN_WORDS_RESPONSE"
                    }
                ],
                [
                    {
                        "text": "Статистика",
                        "callback_data": "$STATISTICS_RESPONSE"
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