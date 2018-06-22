import com.nhaarman.mockito_kotlin.*
import io.ghostbuster91.ktm.identifier.*
import org.junit.Assert.assertEquals
import org.junit.Test

class IdentifierResolverTest {

    @Test
    fun shouldDispatchSolvingToSimpleSolver() {
        val dispatcher = IdentifierSolverDispatcher(SimpleIdentifierResolver())
        val identifier = dispatcher.resolverIdentifier(Identifier.Unparsed("com.github.myOrg:myRepo:version"))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
            assertEquals("version", it.version)
        }
    }

    @Test
    fun shouldCascadeFromAliasToSimple() {
        val aliasController = mock<AliasRepository>()
        whenever(aliasController.getAlias(any())).thenReturn("com.github.myOrg:myRepo:version")
        val dispatcher = IdentifierSolverDispatcher(AliasIdentifierResolver(aliasController), SimpleIdentifierResolver())
        val identifier = dispatcher.resolverIdentifier(Identifier.Unparsed("bestToolEver:version"))
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
            assertEquals("version", it.version)
        }
    }

    @Test
    fun shouldSkipFurtherSolvingWhenAlreadyParsed() {
        val identifierSolver = mock<IdentifierSolverDispatcher.IdentifierResolver>()
        val dispatcher = IdentifierSolverDispatcher(identifierSolver)
        val identifier = dispatcher.resolverIdentifier(Identifier.Parsed("com.github.myOrg", "myRepo", "version"))
        verify(identifierSolver, never()).resolve(any())
        identifier.let {
            assertEquals("com.github.myOrg", it.groupId)
            assertEquals("myRepo", it.artifactId)
            assertEquals("version", it.version)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionIfCannotParseIdentifier() {
        val dispatcher = IdentifierSolverDispatcher(SimpleIdentifierResolver())
        dispatcher.resolverIdentifier(Identifier.Unparsed("com.github"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun aliasResolverIdentifierShouldNotBreakIfNoAliasFound() {
        val aliasController = mock<AliasRepository>()
        whenever(aliasController.getAliases()).thenReturn(emptyList())
        val dispatcher = IdentifierSolverDispatcher(AliasIdentifierResolver(aliasController), SimpleIdentifierResolver())
        dispatcher.resolverIdentifier(Identifier.Unparsed("bestToolEver:version"))
    }
}