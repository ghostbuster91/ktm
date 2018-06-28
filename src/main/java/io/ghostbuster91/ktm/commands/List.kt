package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.core.CliktCommand
import io.ghostbuster91.ktm.components.KtmDirectoryManager

class List(private val directoryManager: KtmDirectoryManager) : CliktCommand() {
    override fun run() {
        directoryManager.getBinaries().forEach(::println)
    }
}