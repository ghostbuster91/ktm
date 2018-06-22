package io.ghostbuster91.ktm.identifier

import io.ghostbuster91.ktm.KtmDirectoryManager


class AliasIdentifierResolver(private val aliasRepository: AliasRepository) : IdentifierSolverDispatcher.IdentifierResolver {

    override fun resolve(identifier: Identifier.Unparsed): Identifier {
        val segments = identifier.text.split(":")
        return if (segments.size == 2) {
            val (name, version) = segments
            aliasRepository.getAlias(name)
                    ?.split(":")
                    ?.let { (groupId, artifactId) ->
                        Identifier.Parsed(groupId, artifactId, version)
                    } ?: identifier
        } else {
            identifier
        }
    }
}

interface AliasRepository {
    fun addAlias(alias: String, name: String)

    fun getAlias(name: String): String?
    fun getAliases() : List<Alias>
}

class AliasFileRepository(private val ktmDirectoryManager: KtmDirectoryManager) : AliasRepository {

    override fun getAliases(): List<Alias> {
        return ktmDirectoryManager.getAliasFile()
                .readLines()
                .map { it.split(" ") }
                .map { (first, second) -> first to second }
    }

    override fun addAlias(alias: String, name: String) {
        ktmDirectoryManager.getAliasFile().writeText("$alias $name")
    }

    override fun getAlias(name: String): String? {
        return getAliases().toMap()[name]
    }
}

typealias Alias = Pair<String, String>