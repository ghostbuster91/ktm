package io.ghostbuster91.ktm.identifier

import io.ghostbuster91.ktm.utils.foldUntil

class IdentifierSolverDispatcher(private val identifierResolvers: List<IdentifierResolver>) {
    fun resolve(identifier: Identifier): Identifier.Parsed {
        val result = identifierResolvers.foldUntil(identifier, { acc, resolver -> resolver.resolve(acc as Identifier.Unparsed) }, { acc -> acc is Identifier.Unparsed })
        return when (result) {
            is Identifier.Parsed -> result
            is Identifier.Unparsed -> throw IllegalArgumentException("Cannot resolver identifier: ${result.text}")
        }
    }

    interface IdentifierResolver {

        fun resolve(identifier: Identifier.Unparsed): Identifier
    }
}

