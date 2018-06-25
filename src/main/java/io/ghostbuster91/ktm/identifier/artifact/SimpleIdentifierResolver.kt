package io.ghostbuster91.ktm.identifier.artifact

import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.IdentifierSolverDispatcher

class SimpleIdentifierResolver : IdentifierSolverDispatcher.IdentifierResolver {
    override fun resolve(identifier: Identifier.Unparsed): Identifier {
        val segments = identifier.text.split(":")
        return if (segments.size == 2) {
            val (g, a) = segments
            Identifier.Parsed(g, a)
        } else {
            identifier
        }
    }
}