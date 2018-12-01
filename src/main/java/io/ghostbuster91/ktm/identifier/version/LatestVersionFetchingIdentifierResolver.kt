package io.ghostbuster91.ktm.identifier.version

import io.ghostbuster91.ktm.components.jitpack.JitPackApi
import io.ghostbuster91.ktm.logger

class LatestVersionFetchingIdentifierResolver(private val latestApi: (String, String) -> JitPackApi.VersionResponse) : VersionSolverDispatcher.VersionResolver {

    override fun resolve(identifier: VersionSolverDispatcher.VersionedIdentifier.Unparsed, version: String?): VersionSolverDispatcher.VersionedIdentifier {
        logger.append("Looking for latest release for $identifier")
        return if (version == null) {
            val response = latestApi(identifier.groupId, identifier.artifactId)
            if (response.version != null) {
                logger.info("Found ${response.version}")
                identifier.toParsed(response.version)
            } else {
                identifier
            }
        } else {
            identifier
        }
    }
}