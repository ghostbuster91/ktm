import io.ghostbuster91.ktm.executeInstallCommand
import org.junit.Test
import java.io.File

class InstallCommandTest {

    @Test
    fun name() {
        val fileDownloader: (String, File) -> Unit = { url, path -> }
        executeInstallCommand("http://github.com/myOrg/myRepo","1.1", fileDownloader)
    }
}