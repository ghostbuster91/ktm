package io.ghostbuster91.ktm.identifier

import io.ghostbuster91.ktm.KtmDirectoryManager


class AliasIdentifierResolver(private val aliasRepository: AliasRepository) : IdentifierSolverDispatcher.IdentifierResolver {

    override fun resolve(identifier: Identifier.Unparsed): Identifier {
        return aliasRepository.getAlias(identifier.text)
                ?.split(":")
                ?.let { (groupId, artifactId) ->
                    identifier.copy(text = "$groupId:$artifactId")
                } ?: identifier
    }
}

interface AliasRepository {
    fun addAlias(alias: String, name: String)

    fun getAlias(name: String): String?
    fun getAliases(): List<Alias>
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