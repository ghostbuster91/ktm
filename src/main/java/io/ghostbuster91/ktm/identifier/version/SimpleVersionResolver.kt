package io.ghostbuster91.ktm.identifier.version

class SimpleVersionResolver : VersionSolverDispatcher.VersionResolver {
    override fun resolve(identifier: VersionSolverDispatcher.VersionedIdentifier.Unparsed, version:String?): VersionSolverDispatcher.VersionedIdentifier {
        return if (version != null) {
            identifier.toParsed(version)
        } else {
            identifier
        }
    }
}