package io.ghostbuster91.ktm.identifier.artifact

import io.ghostbuster91.ktm.components.KtmDirectoryManager

class AliasArtifactResolver(private val aliasRepository: AliasRepository) : ArtifactSolverDispatcher.ArtifactResolver {

    override fun resolve(artifact: ArtifactSolverDispatcher.Artifact.Unparsed): ArtifactSolverDispatcher.Artifact {
        return aliasRepository.getAlias(artifact.text)
                ?.split(":")
                ?.let { (groupId, artifactId) ->
                    artifact.copy(text = "$groupId:$artifactId")
                } ?: artifact
    }
}

interface AliasRepository {
    fun addAlias(alias: String, name: String)

    fun getAlias(name: String): String?
    fun getAliases(): List<Alias>
}

class AliasFileRepository(private val ktmDirectoryManager: KtmDirectoryManager) : AliasRepository {

    override fun getAliases(): List<Alias> {
        val aliasFile = ktmDirectoryManager.getAliasFile()
        return if (aliasFile.exists()) {
            aliasFile
                    .readLines()
                    .map { it.split(" ") }
                    .map { (first, second) -> first to second }
        } else {
            emptyList()
        }
    }

    override fun addAlias(alias: String, name: String) {
        ktmDirectoryManager.getAliasFile().writeText("$alias $name")
    }

    override fun getAlias(name: String): String? {
        return getAliases().toMap()[name]
    }
}

typealias Alias = Pair<String, String>