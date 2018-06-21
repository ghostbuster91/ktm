package io.ghostbuster91.ktm.identifier

class SimpleIdentifierResolver : IdentifierSolverDispatcher.IdentifierResolver {
    override fun resolve(identifier: Identifier.Unparsed): Identifier {
        val segments = identifier.text.split(":")
        return if (segments.size == 3) {
            val (g, a, v) = segments
            Identifier.Parsed(g, a, v)
        } else {
            identifier
        }
    }
}