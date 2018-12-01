package io.ghostbuster91.ktm.identifier.version

class DefaultVersionResolver : VersionSolverDispatcher.VersionResolver {
    override fun resolve(identifier: VersionSolverDispatcher.VersionedIdentifier.Unparsed): VersionSolverDispatcher.VersionedIdentifier {
        return if (identifier.version == null) {
            identifier.toParsed("master-SNAPSHOT")
        } else {
            identifier
        }
    }
}

