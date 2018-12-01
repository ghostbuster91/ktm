package io.ghostbuster91.ktm

import com.nhaarman.mockito_kotlin.*
import io.ghostbuster91.ktm.utils.TestArtifactToLinkTranslator
import io.ghostbuster91.ktm.utils.installTestRepo
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
        installRepo("testOrg:noBinaryRepo")
        verify(logger).error(eq("No binary files found!"), any())
    }

    @Test
    fun shouldDecompressBinaryFile() {
        installRepo("testOrg:validRepo")
        val binaryFile = File(testFolderRuler.root.absolutePath, ".ktm/modules/testOrg/validRepo/master-SNAPSHOT/sample-bin")
        assert(binaryFile.exists())
    }

    @Test
    fun shouldCreateSymlinkToBinary() {
        installRepo("testOrg:validRepo")
        val symlink = File(testFolderRuler.root.absolutePath, ".ktm/bin/validRepo")
        assert(symlink.exists())
        assert(Files.isSymbolicLink(symlink.toPath()))
    }

    @Test
    fun shouldNotInstallLibraryIfLibraryInGivenVersionAlreadyExists() {
        installRepo("testOrg:validRepo")
        reset(logger)
        val artifactToListTranslator = mock<ArtifactToLinkTranslator>()
        installRepo("testOrg:validRepo", artifactToLinkTranslator = artifactToListTranslator)
        verify(logger).info("Library already installed in given version!")
        verifyNoMoreInteractions(artifactToListTranslator)
    }

    @Test
    fun shouldInstallLibraryIfItExistsWhenForceFlagIsPassed() {
        installRepo("testOrg:validRepo")
        reset(logger)
        val artifactToLinkTranslator = spy(TestArtifactToLinkTranslator())
        installRepo("testOrg:validRepo", "--force", artifactToLinkTranslator = artifactToLinkTranslator)
        verify(logger, never()).info("Library already installed in given version!")
        verify(artifactToLinkTranslator).getDownloadLink(any())
    }

    @Test
    fun `should install library in given version when version option provided`() {
        installRepo("testOrg:validRepo", "--version=1.0.0")
        val binaryFile = File(testFolderRuler.root.absolutePath, ".ktm/modules/testOrg/validRepo/1.0.0/sample-bin")
        assert(binaryFile.exists())
    }

    private fun installRepo(
            vararg params: String,
            artifactToLinkTranslator: ArtifactToLinkTranslator = TestArtifactToLinkTranslator()
    ) = installTestRepo(testFolderRuler.root, arrayOf(*params), artifactToLinkTranslator)
}

