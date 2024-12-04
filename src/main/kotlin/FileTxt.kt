package org.example

import java.io.File


fun main() {
    val wordsFile: File = File("words.txt")
    wordsFile.createNewFile()

    val listOfWorlds = wordsFile.readLines()
    for (i in listOfWorlds) {
        println(i)
    }
}
