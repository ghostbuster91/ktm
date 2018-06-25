package io.ghostbuster91.ktm.identifier.artifact

import io.ghostbuster91.ktm.logger

class SearchingArtifactResolver(private val searchApi: (String) -> Map<String, List<String>>) : ArtifactSolverDispatcher.ArtifactResolver {
    override fun resolve(artifact: ArtifactSolverDispatcher.Artifact.Unparsed): ArtifactSolverDispatcher.Artifact {
        logger.append("Looking for $artifact...")
        val searchResult = searchApi(artifact.text)
        return when {
            searchResult.size == 1 -> {
                val result = searchResult.keys.first()
                logger.info("Found $result")
                ArtifactSolverDispatcher.Artifact.Unparsed(result)
            }
            searchResult.size > 1 -> throw RuntimeException("Many artifacts found")
            else -> artifact
        }
    }
}