package io.ghostbuster91.ktm.identifier

import io.ghostbuster91.ktm.JitPackApi

class SearchingIdentifierResolver(private val jitPackApi: JitPackApi) : IdentifierSolverDispatcher.IdentifierResolver {
    override fun resolve(identifier: Identifier.Unparsed): Identifier {
        val searchResult = jitPackApi.search(identifier.text).blockingFirst()
        return when {
            searchResult.size == 1 -> Identifier.Unparsed(searchResult.keys.first())
            searchResult.size > 1 -> throw RuntimeException("Many artifacts found")
            else -> identifier
        }
    }
}