package io.ghostbuster91.ktm.identifier.version

import io.ghostbuster91.ktm.JitPackApi
import io.ghostbuster91.ktm.identifier.VersionSolverDispatcher
import io.ghostbuster91.ktm.identifier.VersionedIdentifier


class LatestVersionFetchingIdentifierResolver(private val jitPackApi: JitPackApi) : VersionSolverDispatcher.VersionResolver {

    override fun resolve(identifier: VersionedIdentifier.Unparsed): VersionedIdentifier {
        return if (identifier.version == null) {
            val response = jitPackApi.latestRelease(identifier.groupId, identifier.artifactId).blockingFirst()
            if (response.version != null) {
                VersionedIdentifier.Parsed(identifier.identifier, response.version)
            } else {
                identifier
            }
        } else {
            identifier
        }
    }
}