package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.core.CliktCommand
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.logger
import java.nio.file.Files
import java.nio.file.Paths

class List(private val directoryManager: KtmDirectoryManager) : CliktCommand("Display all installed packages with corresponding versions") {
    override fun run() {
        directoryManager.getBinaries()
                .map {
                    it.name.baseName to Files.readSymbolicLink(Paths.get(it.name.path)).reversed().drop(1).first()
                }
                .forEach { logger.info("${it.first} --> ${it.second}") }
    }
}