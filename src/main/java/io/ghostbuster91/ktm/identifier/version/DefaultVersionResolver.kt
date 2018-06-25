package io.ghostbuster91.ktm.identifier.version

class DefaultVersionResolver : VersionSolverDispatcher.VersionResolver {
    override fun resolve(identifier: VersionSolverDispatcher.VersionedIdentifier.Unparsed): VersionSolverDispatcher.VersionedIdentifier {
        return if (identifier.version == null) {
            VersionSolverDispatcher.VersionedIdentifier.Parsed(groupId = identifier.groupId, artifactId = identifier.artifactId, version = "master-SNAPSHOT")
        } else {
            identifier
        }
    }
}

