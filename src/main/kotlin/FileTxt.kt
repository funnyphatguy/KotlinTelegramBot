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

fun main() {

    val dictionary = loadDictionary()

    val totalCount = dictionary.size
    val learnedCount = dictionary.filter { it.correctAnswersCount >= 3 }.run { size }
    val percent =  (learnedCount.toDouble() / totalCount.toDouble() * 100).toInt()

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
            1 -> println("Вы выбрали пункт \"учить слова\"")
            2 -> println(
                "Вы выбрали пункт \"статистика\" \nВыучено " +
                        "$learnedCount из $totalCount слов | $percent%" + "\n"
            )
            0 -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}







