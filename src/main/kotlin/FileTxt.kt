package org.example

import java.io.File


fun main() {
    val wordsFile: File = File("words.txt")



    val listOfWorlds = wordsFile.forEachLine { println(it) }

    }

