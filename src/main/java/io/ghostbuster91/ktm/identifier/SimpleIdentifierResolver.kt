package io.ghostbuster91.ktm.identifier

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