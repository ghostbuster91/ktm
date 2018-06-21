package io.ghostbuster91.ktm.identifier

class IdentifierSolverDispatcher(vararg identifierResolvers: IdentifierResolver) {

    private val resolvers: List<IdentifierResolver> = identifierResolvers.toList()

    fun resolverIdentifier(identifier: Identifier): Identifier.Parsed {
        val result = resolvers.foldUntil(identifier, { acc, resolver -> resolver.resolve(acc as Identifier.Unparsed) }, { acc -> acc is Identifier.Unparsed })
        when (result) {
            is Identifier.Parsed -> return result
            is Identifier.Unparsed -> throw IllegalArgumentException("Cannot resolver identifier: ${result.text}")
        }
    }

    interface IdentifierResolver {

        fun resolve(identifier: Identifier.Unparsed): Identifier
    }
}


private inline fun <T, R> Iterable<T>.foldUntil(initial: R, operation: (acc: R, T) -> R, until: (acc: R) -> Boolean): R {
    var accumulator = initial
    for (element in this) {
        if (!until(accumulator)) {
            break
        }
        accumulator = operation(accumulator, element)
    }
    return accumulator
}