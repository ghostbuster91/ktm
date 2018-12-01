package io.ghostbuster91.ktm.utils

import io.ghostbuster91.ktm.ArtifactToLinkTranslator
import io.ghostbuster91.ktm.commands.Install
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.components.TarFileDownloader
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import io.ghostbuster91.ktm.identifier.artifact.ArtifactSolverDispatcher
import io.ghostbuster91.ktm.identifier.artifact.SimpleArtifactResolver
import io.ghostbuster91.ktm.identifier.version.DefaultVersionResolver
import io.ghostbuster91.ktm.identifier.version.SimpleVersionResolver
import io.ghostbuster91.ktm.identifier.version.VersionSolverDispatcher
import io.reactivex.Observable
import java.io.File

class TestArtifactToLinkTranslator : ArtifactToLinkTranslator {
    override fun getDownloadLink(identifier: Identifier.Parsed): String {
        return javaClass.classLoader.getResource("${identifier.groupId}/${identifier.artifactId}/${identifier.version}/archive.tar").path
    }
}

fun installTestRepo(rootFile: File, params: Array<String>, artifactToLinkTranslator: ArtifactToLinkTranslator = TestArtifactToLinkTranslator()) {
    Install(KtmDirectoryManager { rootFile },
            artifactToLinkTranslator,
            TarFileDownloader(Observable.never()),
            IdentifierResolver(ArtifactSolverDispatcher(listOf(SimpleArtifactResolver())), VersionSolverDispatcher(listOf(SimpleVersionResolver(), DefaultVersionResolver())))
    ).main(params)
}