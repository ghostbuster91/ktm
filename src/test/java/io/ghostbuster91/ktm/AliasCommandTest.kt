package io.ghostbuster91.ktm

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.ghostbuster91.ktm.commands.Aliases
import io.ghostbuster91.ktm.identifier.artifact.AliasRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AliasCommandTest {
    @JvmField
    @Rule
    val testFolderRuler = TemporaryFolder()

    @Before
    fun setUp() {
        logger = mock()
    }

    @Test
    fun shouldReturnAliasFromAliasRepository() {
        val aliasRepository = mock<AliasRepository>()
        whenever(aliasRepository.getAliases()).thenReturn(listOf("ktm" to "com.ghostbuster91:ktm"))
        Aliases(aliasRepository).main(emptyArray())
        verify(logger).info("ktm" to "com.ghostbuster91:ktm")
    }
}