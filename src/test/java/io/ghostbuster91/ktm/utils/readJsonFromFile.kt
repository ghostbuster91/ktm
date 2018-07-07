package io.ghostbuster91.ktm.utils

fun Any.readJsonFromFile(fileName: String) = javaClass.classLoader.getResource(fileName)
        .readText()