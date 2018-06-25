package io.ghostbuster91.ktm

import com.nhaarman.mockito_kotlin.*
import io.ghostbuster91.ktm.identifier.*
import io.ghostbuster91.ktm.identifier.artifact.AliasIdentifierResolver
import io.ghostbuster91.ktm.identifier.artifact.AliasRepository
import io.ghostbuster91.ktm.identifier.artifact.SimpleIdentifierResolver
import io.ghostbuster91.ktm.identifier.version.DefaultVersionResolver
import io.ghostbuster91.ktm.identifier.version.SimpleVersionResolver
import org.junit.Assert.assertEquals
import org.junit.Test

class IdentifierResolverTest {

    @Test
    fun shouldDispatchSolvingToSimpleSolver() {
        val dispatcher = IdentifierSolverDispatcher(listOf(SimpleIdentifierResolver()))
        val identifier = dispatcher.resolve(Identifier.Unparsed("com.github.myOrg:myRepo"))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
        }
    }

    @Test
    fun shouldCascadeFromAliasToSimple() {
        val aliasController = mock<AliasRepository>()
        whenever(aliasController.getAlias(any())).thenReturn("com.github.myOrg:myRepo")
        val dispatcher = IdentifierSolverDispatcher(listOf(AliasIdentifierResolver(aliasController), SimpleIdentifierResolver()))
        val identifier = dispatcher.resolve(Identifier.Unparsed("bestToolEver:version"))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
        }
    }

    @Test
    fun shouldSkipFurtherSolvingWhenAlreadyParsed() {
        val identifierSolver = mock<IdentifierSolverDispatcher.IdentifierResolver>()
        val dispatcher = IdentifierSolverDispatcher(listOf(identifierSolver))
        val identifier = dispatcher.resolve(Identifier.Parsed("com.github.myOrg", "myRepo"))
        verify(identifierSolver, never()).resolve(any())
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionIfCannotParseIdentifier() {
        val dispatcher = IdentifierSolverDispatcher(listOf(SimpleIdentifierResolver()))
        dispatcher.resolve(Identifier.Unparsed("com.github"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun aliasResolverIdentifierShouldNotBreakIfNoAliasFound() {
        val aliasController = mock<AliasRepository>()
        whenever(aliasController.getAliases()).thenReturn(emptyList())
        val dispatcher = IdentifierSolverDispatcher(listOf(AliasIdentifierResolver(aliasController), SimpleIdentifierResolver()))
        dispatcher.resolve(Identifier.Unparsed("bestToolEver"))
    }

    @Test
    fun whenVersionProvidedJustParseIt() {
        val versionSolverDispatcher = VersionSolverDispatcher(listOf(SimpleVersionResolver()))
        val identifier = versionSolverDispatcher.resolve(VersionedIdentifier.Unparsed(Identifier.Parsed("com.github.myOrg", "myRepo"), "version"))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
            assertEquals("version", it.version)
        }
    }

    @Test
    fun whenVersionNotProvidedShouldUseDefault() {
        val versionSolverDispatcher = VersionSolverDispatcher(listOf(DefaultVersionResolver(), SimpleVersionResolver()))
        val identifier = versionSolverDispatcher.resolve(VersionedIdentifier.Unparsed(Identifier.Parsed("com.github.myOrg","myRepo"), null))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
            assertEquals("master-SNAPSHOT", it.version)
        }
    }
}