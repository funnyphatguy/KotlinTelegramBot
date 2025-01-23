package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long?,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyBoard>>,
)

@Serializable
data class InlineKeyBoard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)


val trainer: LearnWordsTrainer = LearnWordsTrainer()

var currentQuestion: Question? = null

val statistics = trainer.getStatistics()


fun main(args: Array<String>) {


    val json = Json { ignoreUnknownKeys = true }

    val botService = TelegramBotService(json = json, botToken = args[0])

    var lastUpdateId = 0L

    val traners = HashMap<Long, LearnWordsTrainer>()


    while (true) {
        Thread.sleep(2000)
        val responseString: String = botService.getUpdates(lastUpdateId)
        println(responseString)
        val response: Response = botService.json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, json, botService, traners) }
        lastUpdateId = sortedUpdates.last().updateId + 1
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long?
) {
    currentQuestion = trainer.getNextQuestion()
    if (currentQuestion != null
    ) {
        telegramBotService.sendQuestion(chatId, currentQuestion!!)
    } else telegramBotService.sendMessage(chatId, message = "Вы выучили все слова в списке")
}

fun handleUpdate(update: Update, json: Json, botService: TelegramBotService, trainers: HashMap<Long, LearnWordsTrainer>) {

    val message = update.message?.text

    val chatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return

    val data = update.callbackQuery?.data

    val trainer = trainers.getOrPut(chatId) {LearnWordsTrainer("$chatId.txt")}


    if (data != null) {
        if (data.startsWith(CALLBACK_DATA_ANSWER_PREFIX)) {
            val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()

            if (trainer.checkAnswer(userAnswerIndex)) {
                botService.sendMessage(chatId, message = "Правильно!")
                checkNextQuestionAndSend(trainer, botService, chatId)
            } else {
                botService.sendMessage(
                    chatId,
                    message = "Неправильно! ${currentQuestion?.correctAnswer?.original} " +
                            "это ${currentQuestion?.correctAnswer?.translation}"
                )
                checkNextQuestionAndSend(
                    trainer,
                    botService,
                    chatId
                )
            }
        } else if (data.lowercase() == LEARN_WORDS_RESPONSE_PREFIX)
            checkNextQuestionAndSend(trainer, botService, chatId)
    }

    if (data?.lowercase() == BACK_PREFIX) {
        botService.sendMenu(chatId)
    }

    if (data?.lowercase() == STATISTICS_RESPONSE_PREFIX) {
        botService.sendMessage(
            chatId,
            message = "Выучено ${statistics.learnedCount} " +
                    "из ${statistics.totalCount} слов | ${statistics.percent}%"
        )
    }

    if (message?.lowercase() == "start") {
        botService.sendMessage(chatId, message = "Hello!")
    }

    if (message?.lowercase() == "/start") {
        botService.sendMenu(chatId)
    }
}

