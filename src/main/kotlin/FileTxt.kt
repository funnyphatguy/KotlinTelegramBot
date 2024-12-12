package org.example

import java.io.File

data class Word(
    val original: String,
    val translation: String,
    val correctAnswersCount: Int = 0
)

var wordsFile = File("words.txt")

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
        dictionary.forEach { word -> // Здесь word — это текущий объект Word из списка
            out.println("${word.original}|${word.translation}|${word.correctAnswersCount}")
        }
    }
}

fun main() {

    val dictionary = loadDictionary()

    val notLearnedList = dictionary.filter { it.correctAnswersCount <= 2 }

    val questionWords = notLearnedList.shuffled().map { it.translation }.take(4)
    val correctAnswer = notLearnedList.map { it.original }.random()

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
                println(
                    """
                        
             ${correctAnswer}:
             1 – ${questionWords[0]}
             2 – ${questionWords[1]}
             3 – ${questionWords[2]}
             4 - ${questionWords[3]}
             ----------
             0 - Меню
                      """.trimIndent()
                )
                val userAnswerInput = readln().toInt()
                when (userAnswerInput) {
                    0 -> continue
                    in 1..4 -> {
                        val correctAnswerId = questionWords.indexOf(correctAnswer)
                        if ((correctAnswerId+1) == userAnswerInput) {
                            println("Правильно!")
                            val correctAnswersCountUpdate = notLearnedList.find { it.original == correctAnswer }
                                ?.let { it.copy(correctAnswersCount = it.correctAnswersCount + 1) }
                            saveDictionary(dictionary)
                        } else
                            println("Неправильно! $correctAnswer – это ${questionWords[userAnswerInput - 1]}")
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



