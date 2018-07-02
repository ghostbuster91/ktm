package io.ghostbuster91.ktm.identifier.version

import io.ghostbuster91.ktm.components.jitpack.JitPackApi
import io.ghostbuster91.ktm.logger

class LatestVersionFetchingIdentifierResolver(private val latestApi: (String, String) -> JitPackApi.VersionResponse) : VersionSolverDispatcher.VersionResolver {

    override fun resolve(identifier: VersionSolverDispatcher.VersionedIdentifier.Unparsed): VersionSolverDispatcher.VersionedIdentifier {
        logger.append("Looking for latest release for $identifier")
        return if (identifier.version == null) {
            val response = latestApi(identifier.groupId, identifier.artifactId)
            if (response.version != null) {
                logger.info("Found ${response.version}")
                VersionSolverDispatcher.VersionedIdentifier.Parsed(groupId = identifier.groupId, artifactId = identifier.artifactId, version = response.version)
            } else {
                identifier
            }
        } else {
            identifier
        }
    }
}