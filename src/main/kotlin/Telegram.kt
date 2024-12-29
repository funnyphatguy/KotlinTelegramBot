package org.example

fun main(args: Array<String>) {

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

        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value
        println("Chat ID: $chatId")

        val data = dataRegex.find(updates)?.groups?.get(1)?.value
        println("Дата ${data}")

        val trainer: LearnWordsTrainer = LearnWordsTrainer()


        val statistics = trainer.getStatistics()
        println(statistics)


        if (data?.lowercase() == STATISTICS_RESPONSE && chatId != null) {
            botService.sendMessage(chatId, messageText = "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent}%")
        }

        if (data?.lowercase() == STATISTICS_RESPONSE && chatId != null) {
            botService.sendMessage(chatId, messageText = "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent}%")
        }

        if (text?.lowercase() == "hello" && chatId != null) {
            botService.sendMessage(chatId, messageText = "Hello")
        }
        if (text?.lowercase() == "menu" && chatId != null) {
            botService.sendMenu(chatId)
        }
    }
}

