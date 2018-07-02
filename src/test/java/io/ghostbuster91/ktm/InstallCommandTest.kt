package io.ghostbuster91.ktm

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
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

    private fun installTestRepo(name: String) = installTestRepo(name, testFolderRuler.root)
}

