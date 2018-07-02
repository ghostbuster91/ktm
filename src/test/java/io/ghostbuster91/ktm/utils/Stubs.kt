package io.ghostbuster91.ktm.utils

import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands
import io.ghostbuster91.ktm.ArtifactToLinkTranslator
import io.ghostbuster91.ktm.commands.Install
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.components.TarFileDownloader
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import io.ghostbuster91.ktm.identifier.artifact.SimpleArtifactResolver
import io.ghostbuster91.ktm.identifier.version.DefaultVersionResolver
import io.reactivex.Observable
import java.io.File

class TestCommand : NoRunCliktCommand()

class TestArtifactToLinkTranslator : ArtifactToLinkTranslator {
    override fun getDownloadLink(identifier: Identifier.Parsed): String {
        return javaClass.classLoader.getResource("${identifier.groupId}/${identifier.artifactId}/${identifier.version}/archive.tar").path
    }
}

fun installTestRepo(name: String, rootFile: File) {
    TestCommand().subcommands(Install(KtmDirectoryManager { rootFile },
            TestArtifactToLinkTranslator(),
            TarFileDownloader(Observable.never()),
            IdentifierResolver(listOf(SimpleArtifactResolver()), listOf(DefaultVersionResolver()))
    )).main(arrayOf("install", name))
}