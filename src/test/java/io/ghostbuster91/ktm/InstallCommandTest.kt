package io.ghostbuster91.ktm

import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.components.TarFileDownloader
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import io.ghostbuster91.ktm.identifier.artifact.ArtifactSolverDispatcher
import io.ghostbuster91.ktm.identifier.version.VersionSolverDispatcher
import io.ghostbuster91.ktm.identifier.artifact.SimpleArtifactResolver
import io.ghostbuster91.ktm.identifier.version.SimpleVersionResolver
import io.reactivex.Observable
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Files

class InstallCommandTest {

    @JvmField
    @Rule
    val testFolderRuler = TemporaryFolder()

    @Test()
    fun shouldThrowExceptionIfArchiveDoesNotContainAnyBinaryFile() {
        assertThrowMessage("No binary files found!") {
            install({ javaClass.classLoader.getResource("sample-file.tar").path })
        }
    }

    @Test
    fun shouldDecompressBinaryFile() {
        install({ javaClass.classLoader.getResource("sample-bin.tar").path })
        val binaryFile = File(testFolderRuler.root.absolutePath, ".ktm/modules/com.github.myOrg/myRepo/1.1/sample-bin")
        assert(binaryFile.exists())
    }

    @Test
    fun shouldCreateSymlinkToBinary() {
        install({ javaClass.classLoader.getResource("sample-bin.tar").path })
        val symlink = File(testFolderRuler.root.absolutePath, ".ktm/bin/myRepo")
        assert(symlink.exists())
        assert(Files.isSymbolicLink(symlink.toPath()))
    }

    private fun install(artifactToLink: (Identifier.Parsed) -> String) {
        installer(
                IdentifierResolver(listOf(SimpleArtifactResolver()),listOf(SimpleVersionResolver())),
                KtmDirectoryManager { testFolderRuler.root },
                ArtifactToLinkTranslator(f = artifactToLink),
                TarFileDownloader(Observable.never())
        ).invoke(Identifier.Unparsed("com.github.myOrg:myRepo"), "1.1")
    }
}


fun assertThrowMessage(message: String, f: () -> Unit) = assertThrow(f, { this.message == message }) {
    """Incorrect exception message:
       expecting: "$message"
       but was: "${it.message}" """ + it.printStackTrace()
}

fun assertThrow(f: () -> Unit, matcher: Exception.() -> Boolean, message: (Exception) -> String) {
    try {
        f()
    } catch (ex: Exception) {
        assert(ex.matcher(), { message(ex) })
    }
}