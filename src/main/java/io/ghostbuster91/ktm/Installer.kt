package io.ghostbuster91.ktm

import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import org.apache.commons.vfs2.FileObject

fun installer(identifierResolver: IdentifierResolver,
              ktmDirectoryManager: KtmDirectoryManager,
              artifactToLinkTranslator: ArtifactToLinkTranslator,
              downloader: Downloader
) = { identifier: Identifier.Unparsed, version: String? ->
    install(identifier, version, identifierResolver, ktmDirectoryManager, artifactToLinkTranslator, downloader)
}

private fun install(
        identifier: Identifier.Unparsed,
        version: String?,
        identifierResolver: IdentifierResolver,
        ktmDirectoryManager: KtmDirectoryManager,
        artifactToLinkTranslator: ArtifactToLinkTranslator,
        downloader: Downloader
) {
    val versionedIdentifier = identifierResolver.resolve(identifier, version)
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