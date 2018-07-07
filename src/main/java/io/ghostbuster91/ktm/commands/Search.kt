package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import io.ghostbuster91.ktm.components.jitpack.JitPackApi
import io.ghostbuster91.ktm.logger

class Search(private val jitPackApi: JitPackApi) : CliktCommand() {
    private val query by argument()

    override fun run() {
        val searchResult = jitPackApi.search(query).blockingFirst()
        logger.info(searchResult.map { (k, v) -> "$k --> $v" }.joinToString("\n"))
    }
}