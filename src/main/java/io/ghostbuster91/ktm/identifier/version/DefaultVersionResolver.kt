package io.ghostbuster91.ktm.identifier.version

import io.ghostbuster91.ktm.identifier.VersionSolverDispatcher
import io.ghostbuster91.ktm.identifier.VersionedIdentifier

class DefaultVersionResolver : VersionSolverDispatcher.VersionResolver {
    override fun resolve(identifier: VersionedIdentifier.Unparsed): VersionedIdentifier {
        return if (identifier.version == null) {
            VersionedIdentifier.Parsed(identifier = identifier.identifier, version = "master-SNAPSHOT")
        } else {
            identifier
        }
    }
}

