package io.ghostbuster91.ktm.identifier

class DefaultVersionResolver : VersionSolverDispatcher.VersionResolver {
    override fun resolve(identifier: VersionedIdentifier.Unparsed): VersionedIdentifier {
        return if (identifier.version == null) {
            VersionedIdentifier.Parsed(identifier = identifier.identifier, version = "master-SNAPSHOT")
        } else {
            identifier
        }
    }
}

class SimpleVersionResolver : VersionSolverDispatcher.VersionResolver {
    override fun resolve(identifier: VersionedIdentifier.Unparsed): VersionedIdentifier {
        return if (identifier.version != null) {
            VersionedIdentifier.Parsed(identifier = identifier.identifier, version = identifier.version)
        } else {
            identifier
        }
    }
}