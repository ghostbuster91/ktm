package io.ghostbuster91.ktm.identifier.version

import io.ghostbuster91.ktm.JitPackApi

class LatestVersionFetchingIdentifierResolver(private val jitPackApi: JitPackApi) : VersionSolverDispatcher.VersionResolver {

    override fun resolve(identifier: VersionSolverDispatcher.VersionedIdentifier.Unparsed): VersionSolverDispatcher.VersionedIdentifier {
        return if (identifier.version == null) {
            val response = jitPackApi.latestRelease(identifier.groupId, identifier.artifactId).blockingFirst()
            if (response.version != null) {
                VersionSolverDispatcher.VersionedIdentifier.Parsed(groupId = identifier.groupId, artifactId = identifier.artifactId, version = response.version)
            } else {
                identifier
            }
        } else {
            identifier
        }
    }
}