package org.example

import java.io.File

data class Word(
    val original: String,
    val translation: String,
    val correctAnswersCount: Int = 0
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
            out.println("${word.original}|${word.translation}|${word.correctAnswersCount}")
        }
    }
}

fun main() {

    val dictionary = loadDictionary()

    val notLearnedList = dictionary.filter { it.correctAnswersCount <= 2 }.toMutableList()
    val questionWords = notLearnedList.shuffled().take(4)
    val correctAnswer = notLearnedList.random()

    val totalCount = dictionary.size
    val learnedCount = dictionary.filter { it.correctAnswersCount >= 3 }.run { size }
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
        val click = readln().toInt()
        when (click) {
            1 -> if (notLearnedList.size == 0) println("Все слова в словаре выучены")
            else {
                val variants =
                    questionWords.mapIndexed { index, word -> "${index + 1} - ${word.translation}" }.joinToString(
                        separator = "\n",
                        prefix = "\n${correctAnswer.original}:\n",
                        postfix = "\n-------- \n0 - Меню"
                    )
                println(variants)
                val userAnswerInput = readln().toInt()
                when (userAnswerInput) {
                    0 -> continue
                    in 1..4 -> {
                        val userAnswerId = questionWords[userAnswerInput - 1].translation
                        if (userAnswerId == correctAnswer.translation) {
                            println("Правильно!")

                            val updatedWord =
                                correctAnswer.copy(correctAnswersCount = correctAnswer.correctAnswersCount + 1)
                            notLearnedList[questionWords.indexOf(correctAnswer)] = updatedWord
                            saveDictionary(dictionary)
                        } else {
                            println("Неправильно! ${correctAnswer.original} – это ${correctAnswer.translation}")
                        }
                    }

                    else -> println("Введите номер от 0 до 4")
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





