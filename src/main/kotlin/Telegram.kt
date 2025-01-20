package org.example


fun main(args: Array<String>) {

    val trainer: LearnWordsTrainer = LearnWordsTrainer()

    var currentQuestion: Question? = null

    val statistics = trainer.getStatistics()

    val botService = TelegramBotService(botToken = args[0])

    var updateId = 0

    val updateIdRegex: Regex = """"update_id":(\d+)(?=,)""".toRegex()

    val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()

    val chatIdRegex: Regex = """"chat":\s*\{\s*"id":\s*(\d+)\s*(?=[,}])""".toRegex()

    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = botService.getUpdates(updateId)
        println(updates)

        updateId = updateIdRegex.find(updates)?.groupValues?.get(1)?.toIntOrNull()?.plus(1) ?: continue

        val text = messageRegex.find(updates)?.groups?.get(1)?.value
        if (text != null)
            println("Текст сообщения: $text")

        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toLongOrNull() ?: 0
        println("Chat ID: $chatId")

        val data = dataRegex.find(updates)?.groups?.get(1)?.value
        println("Дата $data")

        fun checkNextQuestionAndSend(
            trainer: LearnWordsTrainer,
            telegramBotService: TelegramBotService,
            chatId: Long
        ) {
            currentQuestion = trainer.getNextQuestion()
            if (currentQuestion != null
            ) {
                telegramBotService.sendQuestion(chatId, currentQuestion!!)
            } else telegramBotService.sendMessage(chatId, messageText = "Вы выучили все слова в списке")
        }

        if (data != null) {
            if (data.startsWith(CALLBACK_DATA_ANSWER_PREFIX)) {
                val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()

                if (trainer.checkAnswer(userAnswerIndex)) {
                    botService.sendMessage(chatId, messageText = "Правильно!")
                    checkNextQuestionAndSend(trainer, botService, chatId)
                } else {
                    botService.sendMessage(
                        chatId,
                        messageText = "Неправильно! ${currentQuestion?.correctAnswer?.original} " +
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
                messageText = "Выучено ${statistics.learnedCount} " +
                        "из ${statistics.totalCount} слов | ${statistics.percent}%"
            )
        }

        if (text?.lowercase() == "start") {
            botService.sendMessage(chatId, messageText = "Hello!")
        }

        if (text?.lowercase() == "menu") {
            botService.sendMenu(chatId)
        }
    }
}