package io.ghostbuster91.ktm.commands

import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import io.ghostbuster91.ktm.logger

class Use(private val directoryManager: KtmDirectoryManager, identifierResolver: IdentifierResolver) : ParsedIdentifierCommand(identifierResolver) {

    override fun run() {
        try {
            require(directoryManager.getLibraryDir(parsed).exists(), { "Library not found. Use \"ktm install $parsed\" to install it first." })
            val binary = directoryManager.getBinary(parsed)
            directoryManager.linkToBinary(parsed, binary)
            logger.info("Done")
        } catch (e: RuntimeException) {
            logger.info(e.message!!)
        } finally {
            logger.info("Done")
        }
    }
}