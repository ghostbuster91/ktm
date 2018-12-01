package io.ghostbuster91.ktm

import com.nhaarman.mockito_kotlin.mock
import io.ghostbuster91.ktm.utils.installTestRepo
import io.ghostbuster91.ktm.utils.update
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Files

class UpdateCommandTest {

    @JvmField
    @Rule
    val testFolderRuler = TemporaryFolder()

    @Before
    fun setUp() {
        logger = mock()
    }

    @Test
    fun `should update installed library to newer version`() {
        installTestRepo(testFolderRuler.root, params = arrayOf("testOrg:validRepo", "--version=1.0.0"))
        update(testFolderRuler.root)
        val binaryFile = File(testFolderRuler.root.absolutePath, ".ktm/modules/testOrg/validRepo/1.2.0/bin/sample-bin")
        assert(binaryFile.exists())
        val symlink = File(testFolderRuler.root.absolutePath, ".ktm/bin/validRepo")
        assert(symlink.exists())
        val value = Files.readSymbolicLink(symlink.toPath())
        assertEquals("1.2.0", value.toList().dropLast(2).last().toString())
    }
}