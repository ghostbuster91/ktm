package io.ghostbuster91.ktm

import com.nhaarman.mockito_kotlin.*
import io.ghostbuster91.ktm.commands.List
import io.ghostbuster91.ktm.components.KtmDirectoryManager
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
        installTestRepo(testFolderRuler.root, arrayOf("testOrg:validRepo"))
        reset(logger)
        listApps()
        verify(logger).info("validRepo --> master-SNAPSHOT")
    }

    @Test
    fun shouldReturnInstalledApplicationsTogetherWithTheirVersions() {
        installTestRepo(testFolderRuler.root, arrayOf("testOrg:validRepo"))
        installTestRepo(testFolderRuler.root, arrayOf("testOrg:otherValidRepo"))
        reset(logger)
        listApps()
        verify(logger).info("validRepo --> master-SNAPSHOT")
        verify(logger).info("otherValidRepo --> master-SNAPSHOT")
    }

    @Test
    fun shouldReturnInstalledApplicationsTogetherWithAllVersions() {
        installTestRepo(testFolderRuler.root, arrayOf("testOrg:validRepo"))
        installTestRepo(testFolderRuler.root, arrayOf("testOrg:validRepo", "--version=1.0.0"))
        installTestRepo(testFolderRuler.root, arrayOf("testOrg:validRepo", "--version=1.2.0"))
        reset(logger)
        listApps("--all")
        verify(logger).info(argWhere<String> { it.contains("testOrg") })
        verify(logger).info(argWhere<String> { it.contains("validRepo") })
        verify(logger).info(argWhere<String> { it.contains("-- 1.2.0") })
        verify(logger).info(argWhere<String> { it.contains("-- 1.0.0") })
        verify(logger).info(argWhere<String> { it.contains("-- master-SNAPSHOT") })
    }

    private fun listApps(vararg params: String) {
        List(KtmDirectoryManager { testFolderRuler.root }).main(params.toList())
    }
}