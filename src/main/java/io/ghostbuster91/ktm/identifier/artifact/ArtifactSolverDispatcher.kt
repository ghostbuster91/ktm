package io.ghostbuster91.ktm.identifier.artifact

import io.ghostbuster91.ktm.utils.foldUntil

class ArtifactSolverDispatcher(private val artifactResolvers: List<ArtifactResolver>) {
    fun resolve(artifact: Artifact): Artifact.Parsed {
        val result = artifactResolvers.foldUntil(artifact, { acc, resolver -> resolver.resolve(acc as Artifact.Unparsed) }, { acc -> acc is Artifact.Unparsed })
        return when (result) {
            is Artifact.Parsed -> result
            is Artifact.Unparsed -> throw ArtifactUnresolved("Cannot resolver artifact: ${result.text}\n" +
                    "Note that only artifacts which have remote tags can be searched by name.\n" +
                    "For other artifacts provide fully qualified name.")
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

    class ArtifactUnresolved(override val message:String) : RuntimeException(message)
}

