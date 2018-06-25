package io.ghostbuster91.ktm

import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.IdentifierSolverDispatcher
import io.ghostbuster91.ktm.identifier.VersionSolverDispatcher
import io.ghostbuster91.ktm.identifier.VersionedIdentifier
import org.apache.commons.vfs2.FileObject

fun installer(identifierSolverDispatcher: IdentifierSolverDispatcher,
              versionSolverDispatcher: VersionSolverDispatcher,
              ktmDirectoryManager: KtmDirectoryManager,
              artifactToLinkTranslator: ArtifactToLinkTranslator,
              downloader: Downloader
) = { identifier: Identifier.Unparsed, version: String? ->
    install(identifier, version, identifierSolverDispatcher, versionSolverDispatcher, ktmDirectoryManager, artifactToLinkTranslator, downloader)
}

private fun install(
        identifier: Identifier.Unparsed,
        version: String?,
        identifierSolverDispatcher: IdentifierSolverDispatcher,
        versionSolverDispatcher: VersionSolverDispatcher,
        ktmDirectoryManager: KtmDirectoryManager,
        artifactToLinkTranslator: ArtifactToLinkTranslator,
        downloader: Downloader
) {
    val parsedIdentifier = identifierSolverDispatcher.resolve(identifier).let { VersionedIdentifier.Unparsed(it,version) }
    val versionedIdentifier: VersionedIdentifier.Parsed = versionSolverDispatcher.resolve(parsedIdentifier)
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