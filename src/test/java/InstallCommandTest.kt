import io.ghostbuster91.ktm.*
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.SimpleIdentifierResolver
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Files

class InstallCommandTest {

    @JvmField
    @Rule
    val testFolderRuler = TemporaryFolder()
    private val parse = { text: String -> SimpleIdentifierResolver().resolve(Identifier.Unparsed(text)) as Identifier.Parsed }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenNoArtifactsFound() {
        assertThrowMessage("Didn't find any artifacts!") {
            executeInstallCommand(parse("github.com.myOrg:myRepo:1.1"), DummyJitPack(""), KtmDirectoryManager { testFolderRuler.root })
        }
    }

    @Test
    fun shouldThrowIfCollectionDoesntContainAnyTarArchive() {
        val buildLog = """
            Files:
            not-relevant
            some-file.ext
            """.trimIndent()
        assertThrowMessage("No tar archives found!") {
            executeInstallCommand(parse("github.com.myOrg:myRepo:1.1"), DummyJitPack(buildLog), KtmDirectoryManager { testFolderRuler.root })
        }
    }

    @Test()
    fun shouldThrowExceptionIfArchiveDoesNotContainAnyBinaryFile() {
        val buildLog = """
            Files:
            not-relevant
            ${javaClass.classLoader.getResource("sample-file.tar").path}
            """.trimIndent()
        assertThrowMessage("No binary files found!") {
            executeInstallCommand(parse("github.com.myOrg:myRepo:1.1"), DummyJitPack(buildLog), KtmDirectoryManager { testFolderRuler.root })
        }
    }

    @Test
    fun shouldDecompressBinaryFile() {
        val buildLog = """
            Files:
            not-relevant
            ${javaClass.classLoader.getResource("sample-bin.tar").path}
            """.trimIndent()
        executeInstallCommand(parse("github.com.myOrg:myRepo:1.1"), DummyJitPack(buildLog), KtmDirectoryManager { testFolderRuler.root })
        val binaryFile = File(testFolderRuler.root.absolutePath, ".ktm/modules/github.com.myOrg:myRepo/1.1/sample-bin")
        assert(binaryFile.exists())
    }

    @Test
    fun shouldCreateSymlinkToBinary() {
        val buildLog = """
            Files:
            not-relevant
            ${javaClass.classLoader.getResource("sample-bin.tar").path}
            """.trimIndent()
        executeInstallCommand(parse("github.com.myOrg:myRepo:1.1"), DummyJitPack(buildLog), KtmDirectoryManager { testFolderRuler.root })
        val symlink = File(testFolderRuler.root.absolutePath, ".ktm/bin/myRepo")
        assert(symlink.exists())
        assert(Files.isSymbolicLink(symlink.toPath()))
    }

    class DummyJitPack(val buildLog: String) : JitPack {
        override fun fetchBuildLog(identifier: Identifier.Parsed): String {
            return buildLog
        }

        override fun getFileUrl(fileName: String) = fileName
    }
}


fun assertThrowMessage(message: String, f: () -> Unit) = assertThrow(f, { this.message == message }) {
    """Incorrect exception message:
       expecting: "$message"
       but was: "${it.message}" """
}

fun assertThrow(f: () -> Unit, matcher: Exception.() -> Boolean, message: (Exception) -> String) {
    try {
        f()
    } catch (ex: Exception) {
        assert(ex.matcher(), { message(ex) })
    }
}