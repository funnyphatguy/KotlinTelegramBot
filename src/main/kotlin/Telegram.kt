package org.example

fun main(args: Array<String>) {

    val botService = TelegramBotService(botToken = args[0])

    var updateId = 0

    val updateIdRegex: Regex = """"update_id":(\d+)(?=,)""".toRegex()

    val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()

    val chatIdRegex: Regex = """"chat":\s*\{\s*"id":\s*(\d+)\s*(?=[,}])""".toRegex()

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

        val trainer: LearnWordsTrainer = LearnWordsTrainer()

        if (text != null) {
            if (text.lowercase() == "hello") {
               botService.sendMessage(chatId, messageText = "Hello")
            } else if (text.lowercase() == "menu") {
                botService.sendMenu(chatId)
            }
        }
    }
}

