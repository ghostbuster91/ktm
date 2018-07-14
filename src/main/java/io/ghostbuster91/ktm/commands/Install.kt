package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import io.ghostbuster91.ktm.ArtifactToLinkTranslator
import io.ghostbuster91.ktm.Downloader
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.components.TarFileDownloader
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import io.ghostbuster91.ktm.logger
import org.apache.commons.vfs2.FileObject

class Install(private val directoryManager: KtmDirectoryManager,
              private val artifactToLinkTranslator: ArtifactToLinkTranslator,
              private val tarFileDownloader: TarFileDownloader,
              identifierResolver: IdentifierResolver) : ParsedIdentifierCommand(identifierResolver, help = "Install or update given package") {

    private val isForce by option("--force").flag("--no-force", default = false)

    override fun run() {
        try {
            if (!directoryManager.getLibraryDir(parsed).exists() || isForce) {
                installer(directoryManager, artifactToLinkTranslator, tarFileDownloader)(parsed)
            } else {
                logger.info("Library already installed in given version!")
            }
        } catch (e: RuntimeException) {
            logger.error(e.message ?: "Empty message", e)
        } finally {
            logger.info("Done")
        }
    }
}

private fun installer(ktmDirectoryManager: KtmDirectoryManager,
                      artifactToLinkTranslator: ArtifactToLinkTranslator,
                      downloader: Downloader
) = { parsed: Identifier.Parsed ->
    install(ktmDirectoryManager, artifactToLinkTranslator, downloader, parsed)
}

private fun install(
        ktmDirectoryManager: KtmDirectoryManager,
        artifactToLinkTranslator: ArtifactToLinkTranslator,
        downloader: Downloader,
        versionedIdentifier: Identifier.Parsed
) {
    val libraryDir = ktmDirectoryManager.getLibraryDir(versionedIdentifier)
    val downloadLink = artifactToLinkTranslator.getDownloadLink(versionedIdentifier)
    val binaryFile = downloader.download(downloadLink, libraryDir)
    binaryFile.markAsExecutable()
    ktmDirectoryManager.linkToBinary(versionedIdentifier, binaryFile)
}


private fun FileObject.markAsExecutable(): FileObject {
    logger.info("Making executable")
    setExecutable(true, true)
    return this
}