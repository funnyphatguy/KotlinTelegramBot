package org.example

import java.io.File

data class Word(
    val original: String,
    val translation: String,
    val correctAnswersCount: Int = 0
)

fun main() {
    val wordsFile = File("words.txt")

    val dictionary: MutableList<Word> = mutableListOf()

    val list = wordsFile.forEachLine { line ->
        val cells = line.split("|")
        val word = Word(
            original = cells[0],
            translation = cells[1],
            correctAnswersCount = cells.getOrNull(2)?.toIntOrNull() ?: 0
        )
        dictionary.add(word).also { println(dictionary) }
    }
}







