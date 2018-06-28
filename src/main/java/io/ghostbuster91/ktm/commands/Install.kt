package io.ghostbuster91.ktm.commands

import io.ghostbuster91.ktm.ArtifactToLinkTranslator
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.components.TarFileDownloader
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import io.ghostbuster91.ktm.installer
import io.ghostbuster91.ktm.logger

class Install(private val directoryManager: KtmDirectoryManager,
              private val artifactToLinkTranslator: ArtifactToLinkTranslator,
              private val tarFileDownloader: TarFileDownloader,
              identifierResolver: IdentifierResolver) : ParsedIdentifierCommand(identifierResolver) {

    override fun run() {
        try {
            if (!directoryManager.getLibraryDir(parsed).exists()) {
                installer(directoryManager, artifactToLinkTranslator, tarFileDownloader)(parsed)
            } else {
                logger.info("Library already installed in given version!")
            }
        } catch (e: RuntimeException) {
            logger.info(e.message!!)
        } finally {
            logger.info("Done")
        }
    }
}