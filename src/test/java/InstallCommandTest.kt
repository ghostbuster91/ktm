import io.ghostbuster91.ktm.JitPack
import io.ghostbuster91.ktm.executeInstallCommand
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class InstallCommandTest {

    @JvmField
    @Rule
    val testFolderRuler = TemporaryFolder()

    @Test
    fun name() {
        executeInstallCommand("github.com/myOrg/myRepo", "1.1", downloader, { testFolderRuler.root },{})
    }

    val downloader = object : JitPack {
        override fun fetchBuildLog(name: String, version: String): String {
            return ""
        }

        override fun downloadFile(name: String, version: String, file: String, path: File, updateProgress: (Int) -> Unit) {
        }
    }
}