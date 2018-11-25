package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.core.CliktCommand
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.logger

class List(private val directoryManager: KtmDirectoryManager) : CliktCommand("Display all installed packages with corresponding versions") {
    override fun run() {
        directoryManager.getActiveModules()
                .forEach { logger.info("${it.artifactId} --> ${it.version}") }
    }
}