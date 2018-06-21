package io.ghostbuster91.ktm.identifier

import io.ghostbuster91.ktm.KtmDirectoryManager


class AliasIdentifierResolver(private val aliasController: AliasController) : IdentifierSolverDispatcher.IdentifierResolver {

    override fun resolve(identifier: Identifier.Unparsed): Identifier {
        val segmentsCount = identifier.text.split(":").size
        return if (segmentsCount == 2) {
            val aliases = aliasController.getAliases()
                    .map { (first, second) -> first to second }
                    .toMap()
            val (name, version) = identifier.text.split(":")
            val (groupId, artifactId) = aliases[name]!!.split(":")
            Identifier.Parsed(groupId, artifactId, version)

        } else {
            identifier
        }
    }
}

interface AliasController {
    fun addAlias(alias: String, name: String)

    fun getAliases(): List<Alias>
}

class AliasFileController(private val ktmDirectoryManager: KtmDirectoryManager) : AliasController {

    override fun getAliases(): List<Alias> {
        return ktmDirectoryManager.getAliasFile()
                .readLines()
                .map { it.split(":") }
                .map { (first, second) -> first to second }
    }

    override fun addAlias(alias: String, name: String) {
        ktmDirectoryManager.getAliasFile().writeText("$alias $name")
    }
}

typealias Alias = Pair<String, String>