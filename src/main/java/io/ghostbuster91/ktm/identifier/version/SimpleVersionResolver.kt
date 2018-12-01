package io.ghostbuster91.ktm.identifier.version

class SimpleVersionResolver : VersionSolverDispatcher.VersionResolver {
    override fun resolve(identifier: VersionSolverDispatcher.VersionedIdentifier.Unparsed): VersionSolverDispatcher.VersionedIdentifier {
        return if (identifier.version != null) {
            identifier.toParsed(identifier.version)
        } else {
            identifier
        }
    }
}