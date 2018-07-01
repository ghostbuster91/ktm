package io.ghostbuster91.ktm

import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.ghostbuster91.ktm.commands.Install
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.components.TarFileDownloader
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import io.ghostbuster91.ktm.identifier.artifact.SimpleArtifactResolver
import io.ghostbuster91.ktm.identifier.version.DefaultVersionResolver
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Files

class InstallCommandTest {

    @JvmField
    @Rule
    val testFolderRuler = TemporaryFolder()

    @Before
    fun setUp() {
        logger = mock()
    }

    @Test()
    fun shouldThrowExceptionIfArchiveDoesNotContainAnyBinaryFile() {
        installTestRepo("testOrg:noBinaryRepo")
        verify(logger).error(eq("No binary files found!"), any())
    }

    @Test
    fun shouldDecompressBinaryFile() {
        installTestRepo("testOrg:validRepo")
        val binaryFile = File(testFolderRuler.root.absolutePath, ".ktm/modules/testOrg/validRepo/master-SNAPSHOT/sample-bin")
        assert(binaryFile.exists())
    }

    @Test
    fun shouldCreateSymlinkToBinary() {
        installTestRepo("testOrg:validRepo")
        val symlink = File(testFolderRuler.root.absolutePath, ".ktm/bin/validRepo")
        assert(symlink.exists())
        assert(Files.isSymbolicLink(symlink.toPath()))
    }

    private fun installTestRepo(name: String) {
        TestCommand().subcommands(Install(KtmDirectoryManager { testFolderRuler.root },
                TestArtifactToLinkTranslator(),
                TarFileDownloader(Observable.never()),
                IdentifierResolver(listOf(SimpleArtifactResolver()), listOf(DefaultVersionResolver()))
        )).main(arrayOf("install", name))
    }
}

class TestCommand : NoRunCliktCommand()

class TestArtifactToLinkTranslator : ArtifactToLinkTranslator {
    override fun getDownloadLink(identifier: Identifier.Parsed): String {
        return javaClass.classLoader.getResource("${identifier.groupId}/${identifier.artifactId}/${identifier.version}/archive.tar").path
    }
}