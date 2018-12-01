package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.core.CliktCommand
import io.ghostbuster91.ktm.ArtifactToLinkTranslator
import io.ghostbuster91.ktm.Downloader
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.version.VersionSolverDispatcher
import io.ghostbuster91.ktm.logger

class Update(
        private val ktmDirectoryManager: KtmDirectoryManager,
        private val artifactToLinkTranslator: ArtifactToLinkTranslator,
        private val versionSolverDispatcher: VersionSolverDispatcher,
        private val downloader: Downloader
) : CliktCommand("Update all installed packages") {

    override fun run() {
        ktmDirectoryManager.getActiveModules()
                .also { modules ->
                    logger.info("Following active modules were found:")
                    modules.forEach {
                        println("${it.groupId}:${it.artifactId}:${it.version}")
                    }
                }
                .also { println("Checking for updates...") }
                .map { versionSolverDispatcher.resolve(it.asVersioned()).asIdentifier() }
                .filter {
                    val exists = ktmDirectoryManager.getLibraryDir(it).exists()
                    if (exists) {
                        println("${it.name} is up to date")
                    }
                    !exists
                }
                .forEach {
                    println("Updating ${it.name} to ${it.version}")
                    installer(ktmDirectoryManager, artifactToLinkTranslator, downloader)(it)
                }
    }

    private fun VersionSolverDispatcher.VersionedIdentifier.Parsed.asIdentifier() =
            Identifier.Parsed(groupId, artifactId, version)

    private fun Identifier.Parsed.asVersioned() =
            VersionSolverDispatcher.VersionedIdentifier.Unparsed(groupId, artifactId, version)
}