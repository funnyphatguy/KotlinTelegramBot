package org.example
import java.io.File

fun Question.asConsoleString(): String {
    val variants =
        this.questionWords.mapIndexed { index, word -> "${index + 1} - ${word.translation}" }.joinToString(
            separator = "\n",
            prefix = "\n${this.correctAnswer.original}:\n",
            postfix = "\n-------- \n0 - Меню"
        )
    return variants
}

val wordsFile = File("words.txt")

fun main() {
    val trainer = try {
        LearnWordsTrainer(3, 4, 2)
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }

    while (true) {
        println(
            """
             Меню:
             1 – Учить слова
             2 – Статистика
             0 – Выход
             """.trimIndent()
        )

        when (val click = readln().toIntOrNull()) {
            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion()
                    if (question == null) {
                        println("Все слова в словаре выучены")
                        break
                    }

                    println((question.asConsoleString()))

                    when (val userAnswerInput = readln().toIntOrNull()) {
                        0 -> break

                        in 1..4 -> {

                            if (userAnswerInput != null) {
                                if (trainer.checkAnswer(userAnswerInput.minus(1))) {
                                    println("Правильно!")

                                } else
                                    println("Неправильно! ${question.correctAnswer.original} – это ${question.correctAnswer.translation}")
                            }
                        }

                        else -> println("Введите номер от 0 до 4")
                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println(
                    "Вы выбрали пункт \"статистика\" \nВыучено " +
                            "${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent}%" + "\n"
                )
            }

            0 -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}