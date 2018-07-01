package io.ghostbuster91.ktm

import com.nhaarman.mockito_kotlin.*
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.artifact.AliasArtifactResolver
import io.ghostbuster91.ktm.identifier.artifact.AliasRepository
import io.ghostbuster91.ktm.identifier.artifact.ArtifactSolverDispatcher
import io.ghostbuster91.ktm.identifier.artifact.ArtifactSolverDispatcher.*
import io.ghostbuster91.ktm.identifier.artifact.SimpleArtifactResolver
import io.ghostbuster91.ktm.identifier.version.DefaultVersionResolver
import io.ghostbuster91.ktm.identifier.version.SimpleVersionResolver
import io.ghostbuster91.ktm.identifier.version.VersionSolverDispatcher
import io.ghostbuster91.ktm.identifier.version.VersionSolverDispatcher.*
import org.junit.Assert.assertEquals
import org.junit.Test

class IdentifierResolverTest {

    @Test
    fun shouldDispatchSolvingToSimpleSolver() {
        val dispatcher = ArtifactSolverDispatcher(listOf(SimpleArtifactResolver()))
        val identifier = dispatcher.resolve(Artifact.Unparsed("com.github.myOrg:myRepo"))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
        }
    }

    @Test
    fun shouldCascadeFromAliasToSimple() {
        val aliasController = mock<AliasRepository>()
        whenever(aliasController.getAlias(any())).thenReturn("com.github.myOrg:myRepo")
        val dispatcher = ArtifactSolverDispatcher(listOf(AliasArtifactResolver(aliasController), SimpleArtifactResolver()))
        val identifier = dispatcher.resolve(Artifact.Unparsed("bestToolEver:version"))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
        }
    }

    @Test
    fun shouldSkipFurtherSolvingWhenAlreadyParsed() {
        val identifierSolver = mock<ArtifactSolverDispatcher.ArtifactResolver>()
        val dispatcher = ArtifactSolverDispatcher(listOf(identifierSolver))
        val identifier = dispatcher.resolve(Artifact.Parsed("com.github.myOrg", "myRepo"))
        verify(identifierSolver, never()).resolve(any())
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionIfCannotParseIdentifier() {
        val dispatcher = ArtifactSolverDispatcher(listOf(SimpleArtifactResolver()))
        dispatcher.resolve(Artifact.Unparsed("com.github"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun aliasResolverIdentifierShouldNotBreakIfNoAliasFound() {
        val aliasController = mock<AliasRepository>()
        whenever(aliasController.getAliases()).thenReturn(emptyList())
        val dispatcher = ArtifactSolverDispatcher(listOf(AliasArtifactResolver(aliasController), SimpleArtifactResolver()))
        dispatcher.resolve(Artifact.Unparsed("bestToolEver"))
    }

    @Test
    fun whenVersionProvidedJustParseIt() {
        val versionSolverDispatcher = VersionSolverDispatcher(listOf(SimpleVersionResolver()))
        val identifier = versionSolverDispatcher.resolve(VersionedIdentifier.Unparsed(groupId = "com.github.myOrg",artifactId =  "myRepo",version =  "version"))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
            assertEquals("version", it.version)
        }
    }

    @Test
    fun whenVersionNotProvidedShouldUseDefault() {
        val versionSolverDispatcher = VersionSolverDispatcher(listOf(DefaultVersionResolver(), SimpleVersionResolver()))
        val identifier = versionSolverDispatcher.resolve(VersionedIdentifier.Unparsed(groupId = "com.github.myOrg",artifactId =  "myRepo",version =  null))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
            assertEquals("master-SNAPSHOT", it.version)
        }
    }
}