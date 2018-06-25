package io.ghostbuster91.ktm.identifier

import io.ghostbuster91.ktm.identifier.artifact.ArtifactSolverDispatcher
import io.ghostbuster91.ktm.identifier.version.VersionSolverDispatcher

class IdentifierResolver(
        artifactResolvers: List<ArtifactSolverDispatcher.ArtifactResolver>,
        versionResolvers: List<VersionSolverDispatcher.VersionResolver>
) {
    private val identifierSolverDispatcher = ArtifactSolverDispatcher(artifactResolvers)
    private val versionSolverDispatcher = VersionSolverDispatcher(versionResolvers)

    fun resolve(unparsed: Identifier.Unparsed, version: String?): Identifier.Parsed {
        return unparsed
                .let { ArtifactSolverDispatcher.Artifact.Unparsed(it.text) }
                .let(identifierSolverDispatcher::resolve)
                .let { VersionSolverDispatcher.VersionedIdentifier.Unparsed(groupId = it.groupId, artifactId = it.artifactId, version = version) }
                .let(versionSolverDispatcher::resolve)
                .let { Identifier.Parsed(it.groupId, it.artifactId, it.version) }
    }
}