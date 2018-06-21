package io.ghostbuster91.ktm.identifier

import io.ghostbuster91.ktm.KtmDirectoryManager


class AliasIdentifierResolver(private val aliasController: AliasController) : IdentifierSolverDispatcher.IdentifierResolver {

    override fun resolve(identifier: Identifier.Unparsed): Identifier {
        val segments = identifier.text.split(":")
        return if (segments.size == 2) {
            val (name, version) = segments
            aliasController.getAlias(name)
                    ?.split(":")
                    ?.let { (groupId, artifactId) ->
                        Identifier.Parsed(groupId, artifactId, version)
                    } ?: identifier
        } else {
            identifier
        }
    }
}

interface AliasController {
    fun addAlias(alias: String, name: String)

    fun getAlias(name: String): String?
    fun getAliases() : List<Alias>
}

class AliasFileController(private val ktmDirectoryManager: KtmDirectoryManager) : AliasController {

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