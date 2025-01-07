package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val BOT_URL = "https://api.telegram.org/bot"
const val LEARN_WORDS_RESPONSE_PREFIX = "learn_words_clicked"
const val STATISTICS_RESPONSE_PREFIX = "statistics_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
const val BACK_PREFIX = "back"

class TelegramBotService(val botToken: String) {

    private val client: HttpClient = HttpClient.newBuilder().build()

    fun sendMessage(chatId: Int, messageText: String): String {
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

    fun sendQuestion(chatId: Int, question: Question): String {

        val urlSendMessage: String = "$BOT_URL$botToken/sendMessage"
        val questionWord = question.questionWords.mapIndexed { index, word -> Pair(index+1, word.translation) }
        val sendAnswerBody = """
    {
        "chat_id": "$chatId",
        "text": "${question.correctAnswer.original}",
        "reply_markup": {
            "inline_keyboard": [
                [
                    {
                        "text": "${questionWord[0].second}",
                        "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}${questionWord[0].first}"
                    }
                ],
                [
                    {
                        "text": "${questionWord[1].second}",
                        "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}${questionWord[1].first}"
                    }
                ],
                [
                    {
                        "text": "${questionWord[2].second}",
                        "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}${questionWord[2].first}"
                    }
                ],
                [
                    {
                        "text": "${questionWord[3].second}",
                        "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}${questionWord[3].first}"
                    }
                ],
                [
                    {
                        "text": "Назад",
                        "callback_data": "${BACK_PREFIX}"
                    }
                ]
            ]
        }
    }
""".trimIndent()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendAnswerBody))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMenu(chatId: Int): String {
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
                        "callback_data": "$LEARN_WORDS_RESPONSE_PREFIX"
                    }
                ],
                [
                    {
                        "text": "Статистика",
                        "callback_data": "$STATISTICS_RESPONSE_PREFIX"
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