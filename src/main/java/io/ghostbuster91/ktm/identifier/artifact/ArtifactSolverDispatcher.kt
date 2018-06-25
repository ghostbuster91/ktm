package io.ghostbuster91.ktm.identifier.artifact

import io.ghostbuster91.ktm.utils.foldUntil

class ArtifactSolverDispatcher(private val artifactResolvers: List<ArtifactResolver>) {
    fun resolve(artifact: Artifact): Artifact.Parsed {
        val result = artifactResolvers.foldUntil(artifact, { acc, resolver -> resolver.resolve(acc as Artifact.Unparsed) }, { acc -> acc is Artifact.Unparsed })
        return when (result) {
            is Artifact.Parsed -> result
            is Artifact.Unparsed -> throw IllegalArgumentException("Cannot resolver artifact: ${result.text}")
        }
    }

    interface ArtifactResolver {

        fun resolve(artifact: Artifact.Unparsed): Artifact
    }

    sealed class Artifact {
        data class Parsed(val groupId: String, val artifactId: String) : Artifact() {
            override fun toString() = "$groupId:$artifactId"
        }

        data class Unparsed(val text: String) : Artifact(){
            override fun toString() = text
        }
    }
}

