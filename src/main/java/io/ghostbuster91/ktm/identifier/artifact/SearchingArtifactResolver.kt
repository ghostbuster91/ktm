package io.ghostbuster91.ktm.identifier.artifact

import io.ghostbuster91.ktm.JitPackApi

class SearchingArtifactResolver(private val jitPackApi: JitPackApi) : ArtifactSolverDispatcher.ArtifactResolver {
    override fun resolve(artifact: ArtifactSolverDispatcher.Artifact.Unparsed): ArtifactSolverDispatcher.Artifact {
        val searchResult = jitPackApi.search(artifact.text).blockingFirst()
        return when {
            searchResult.size == 1 -> ArtifactSolverDispatcher.Artifact.Unparsed(searchResult.keys.first())
            searchResult.size > 1 -> throw RuntimeException("Many artifacts found")
            else -> artifact
        }
    }
}