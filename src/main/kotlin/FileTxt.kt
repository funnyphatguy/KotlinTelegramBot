package org.example

import java.io.File

data class Word(
    val original: String,
    val translation: String,
    var correctAnswersCount: Int = 0
)

val wordsFile = File("words.txt")

fun loadDictionary(): List<Word> {
    val dictionary: MutableList<Word> = mutableListOf()
    wordsFile.forEachLine { line ->
        val cells = line.split("|")
        val word = Word(
            original = cells[0],
            translation = cells[1],
            correctAnswersCount = cells.getOrNull(2)?.toIntOrNull() ?: 0
        )
        dictionary.add(word)
    }
    return dictionary
}

fun saveDictionary(dictionary: List<Word>) {
    wordsFile.printWriter().use { out ->
        dictionary.forEach { word ->
            out.write("${word.original}|${word.translation}|${word.correctAnswersCount}\n")
        }
    }
}

const val MAXIMUM_VARIANTS = 4
const val WORLD_LEARNING_TARGET = 2
const val LEARNING_WORD_QUANTITY = 3

fun main() {

    val dictionary = loadDictionary().toMutableList()

    val totalCount = dictionary.size
    val learnedCount = dictionary.filter { it.correctAnswersCount >= LEARNING_WORD_QUANTITY }.run { size }
    val percent = (learnedCount.toDouble() / totalCount.toDouble() * 100).toInt()

    while (true) {
        println(
            """
             Меню: 
             1 – Учить слова
             2 – Статистика
             0 – Выход
             """.trimIndent()
        )

        when (val click = readln().toInt()) {
            1 -> {
                while (true) {
                    val notLearnedList = dictionary.filter { it.correctAnswersCount <= WORLD_LEARNING_TARGET }
                    if (notLearnedList.isEmpty()) {
                        println("Все слова в словаре выучены")
                        break
                    }

                    val questionWords = notLearnedList.shuffled().take(MAXIMUM_VARIANTS)
                    val correctAnswer = notLearnedList.random()

                    val variants =
                        questionWords.mapIndexed { index, word -> "${index + 1} - ${word.translation}" }.joinToString(
                            separator = "\n",
                            prefix = "\n${correctAnswer.original}:\n",
                            postfix = "\n-------- \n0 - Меню"
                        )
                    println(variants)

                    when (val userAnswerInput = readln().toInt()) {
                        0 -> break
                        in 1..4 -> {
                            val userAnswerId = questionWords.indexOf(correctAnswer)
                            if (userAnswerInput.minus(1) == userAnswerId) {
                                println("Правильно!")
                                correctAnswer.correctAnswersCount++
                                saveDictionary(dictionary)
                            } else
                                println("Неправильно! ${correctAnswer.original} – это ${correctAnswer.translation}")
                        }

                        else -> println("Введите номер от 0 до 4")
                    }
                }
            }

            2 -> println(
                "Вы выбрали пункт \"статистика\" \nВыучено " +
                        "$learnedCount из $totalCount слов | $percent%" + "\n"
            )

            0 -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}