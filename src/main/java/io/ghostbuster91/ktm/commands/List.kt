package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.logger

class List(private val directoryManager: KtmDirectoryManager) : CliktCommand("Display all installed packages with corresponding versions") {

    private val all by option("--all").flag(default = false)

    override fun run() {
        if (all) {
            directoryManager.getAllModules()
                    .groupBy { it.groupId }
                    .mapValues { it.value.groupBy { it.artifactId } }
                    .forEach { (group, artifacts) ->
                        logger.info("$group:".indent(5))
                        artifacts.forEach { (artifact, versions) ->
                            logger.info("$artifact:".indent(10))
                            versions.forEach {
                                logger.info("-- ${it.version}".indent(15))
                            }
                        }
                    }
        } else {
            directoryManager.getActiveModules()
                    .forEach { logger.info("${it.artifactId} --> ${it.version}") }
        }
    }

    private fun String.indent(size: Int): String {
        require(size > 1)
        return this + (0..size).joinToString(" ") { "" }
    }
}