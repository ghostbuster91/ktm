package io.ghostbuster91.ktm.identifier.artifact

class SimpleArtifactResolver : ArtifactSolverDispatcher.ArtifactResolver {
    override fun resolve(artifact: ArtifactSolverDispatcher.Artifact.Unparsed): ArtifactSolverDispatcher.Artifact {
        val segments = artifact.text.split(":")
        return if (segments.size == 2) {
            val (g, a) = segments
            ArtifactSolverDispatcher.Artifact.Parsed(g, a)
        } else {
            artifact
        }
    }
}