package io.ghostbuster91.ktm

import com.nhaarman.mockito_kotlin.*
import io.ghostbuster91.ktm.identifier.*
import org.junit.Assert.assertEquals
import org.junit.Test

class IdentifierResolverTest {

    @Test
    fun shouldDispatchSolvingToSimpleSolver() {
        val dispatcher = IdentifierSolverDispatcher(listOf(SimpleIdentifierResolver()))
        val identifier = dispatcher.resolverIdentifier(Identifier.Unparsed("com.github.myOrg:myRepo"))
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
        val identifier = dispatcher.resolverIdentifier(Identifier.Unparsed("bestToolEver:version"))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
        }
    }

    @Test
    fun shouldSkipFurtherSolvingWhenAlreadyParsed() {
        val identifierSolver = mock<IdentifierSolverDispatcher.IdentifierResolver>()
        val dispatcher = IdentifierSolverDispatcher(listOf(identifierSolver))
        val identifier = dispatcher.resolverIdentifier(Identifier.Parsed("com.github.myOrg", "myRepo"))
        verify(identifierSolver, never()).resolve(any())
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionIfCannotParseIdentifier() {
        val dispatcher = IdentifierSolverDispatcher(listOf(SimpleIdentifierResolver()))
        dispatcher.resolverIdentifier(Identifier.Unparsed("com.github"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun aliasResolverIdentifierShouldNotBreakIfNoAliasFound() {
        val aliasController = mock<AliasRepository>()
        whenever(aliasController.getAliases()).thenReturn(emptyList())
        val dispatcher = IdentifierSolverDispatcher(listOf(AliasIdentifierResolver(aliasController), SimpleIdentifierResolver()))
        dispatcher.resolverIdentifier(Identifier.Unparsed("bestToolEver"))
    }

    @Test
    fun whenVersionProvidedJustParseIt() {
        val identifierSolverDispatcher = IdentifierSolverDispatcher(listOf(SimpleIdentifierResolver()))
        val versionSolverDispatcher = VersionSolverDispatcher(listOf(SimpleVersionResolver()), identifierSolverDispatcher)
        val identifier = versionSolverDispatcher.resolverVersionedIdentifier(Identifier.Unparsed("com.github.myOrg:myRepo"), "version")
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
            assertEquals("version", it.version)
        }
    }

    @Test
    fun whenVersionNotProvidedShouldUseDefault() {
        val identifierSolverDispatcher = IdentifierSolverDispatcher(listOf(SimpleIdentifierResolver()))
        val versionSolverDispatcher = VersionSolverDispatcher(listOf(DefaultVersionResolver(), SimpleVersionResolver()), identifierSolverDispatcher)
        val identifier = versionSolverDispatcher.resolverVersionedIdentifier(Identifier.Unparsed("com.github.myOrg:myRepo"), null)
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
            assertEquals("master-SNAPSHOT", it.version)
        }
    }
}