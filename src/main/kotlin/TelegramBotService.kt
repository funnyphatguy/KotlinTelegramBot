package org.example

import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val BOT_URL = "https://api.telegram.org/bot"
const val LEARN_WORDS_RESPONSE_PREFIX = "learn_words_clicked"
const val STATISTICS_RESPONSE_PREFIX = "statistics_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
const val BACK_PREFIX = "back"

class TelegramBotService(private val json: Json, private val botToken: String) {

    private val client: HttpClient = HttpClient.newBuilder().build()

    fun sendMessage(chatId: Long?, message: String): String {
        val urlSendMessage: String = "$BOT_URL$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = message,
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun getUpdates(updateId: Long): String {
        val urlGetMessage: String = "$BOT_URL$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetMessage)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendQuestion(chatId: Long?, question: Question): String {

        val urlSendMessage: String = "$BOT_URL$botToken/sendMessage"

        val variantsString = question.questionWords
            .mapIndexed { index, word ->
                """
        {
            "text": "${word.translation}",
            "callback_data": "$CALLBACK_DATA_ANSWER_PREFIX$index"
        }
        """.trimIndent()
            }
            .joinToString(",")

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.correctAnswer.original,
            replyMarkup = ReplyMarkup(
                listOf(question.questionWords.mapIndexed { index, word ->
                    InlineKeyBoard(
                        text = word.translation, callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"
                    )
                })
            )
        )

        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMenu(chatId: Long?): String {
        val urlSendMessage: String = "$BOT_URL$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyBoard(text = "Изучать слова", callbackData = LEARN_WORDS_RESPONSE_PREFIX),
                        InlineKeyBoard(text = "Статистика", callbackData = STATISTICS_RESPONSE_PREFIX),
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}