package io.ghostbuster91.ktm

import com.github.ajalt.clikt.core.subcommands
import com.nhaarman.mockito_kotlin.*
import io.ghostbuster91.ktm.commands.List
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.utils.TestCommand
import io.ghostbuster91.ktm.utils.installTestRepo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ListCommandTest {

    @JvmField
    @Rule
    val testFolderRuler = TemporaryFolder()

    @Before
    fun setUp() {
        logger = mock()
    }

    @Test
    fun shouldReturnEmptyListIfNoInstallCommandFound() {
        listApps()
        verify(logger, never()).info(any())
    }

    @Test
    fun shouldReturnInstalledApplicationTogetherWithItsVersion() {
        installTestRepo("testOrg:validRepo", testFolderRuler.root)
        reset(logger)
        listApps()
        verify(logger).info("validRepo --> master-SNAPSHOT")
    }

    private fun listApps() {
        TestCommand().subcommands(List(KtmDirectoryManager { testFolderRuler.root })).main(arrayOf("list"))
    }
}