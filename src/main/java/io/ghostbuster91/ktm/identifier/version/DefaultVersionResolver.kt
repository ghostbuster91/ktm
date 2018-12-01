package io.ghostbuster91.ktm.identifier.version

class DefaultVersionResolver : VersionSolverDispatcher.VersionResolver {
    override fun resolve(identifier: VersionSolverDispatcher.VersionedIdentifier.Unparsed, version: String?): VersionSolverDispatcher.VersionedIdentifier {
        return if (version == null) {
            identifier.toParsed("master-SNAPSHOT")
        } else {
            identifier
        }
    }
}

