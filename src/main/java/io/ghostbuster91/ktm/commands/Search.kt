package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import io.ghostbuster91.ktm.components.jitpack.JitPackApi
import io.ghostbuster91.ktm.logger

class Search(private val jitPackApi: JitPackApi) : CliktCommand(
"""Search jitPack api for given substring in package name

   This command will show you only artifacts which were built by jitPack,
   which means that they were requested to download at least once.
""".trimIndent()) {
    private val query by argument()

    override fun run() {
        val searchResult = jitPackApi.search(query).blockingFirst()
        logger.info(searchResult.map { (k, v) -> "$k --> $v" }.joinToString("\n"))
    }
}