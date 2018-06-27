package io.ghostbuster91.ktm

import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.identifier.Identifier
import org.apache.commons.vfs2.FileObject

fun installer(ktmDirectoryManager: KtmDirectoryManager,
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