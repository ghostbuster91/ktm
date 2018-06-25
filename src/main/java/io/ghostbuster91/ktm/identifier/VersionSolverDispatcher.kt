package io.ghostbuster91.ktm.identifier

import io.ghostbuster91.ktm.utils.foldUntil

class VersionSolverDispatcher(private val versionResolver: List<VersionResolver>) {
    fun resolve(identifier: VersionedIdentifier): VersionedIdentifier.Parsed {
        val versionedIdentifier = versionResolver.foldUntil(identifier, { acc, resolver -> resolver.resolve(acc as VersionedIdentifier.Unparsed) }, { acc -> acc is VersionedIdentifier.Unparsed })
        when (versionedIdentifier) {
            is VersionedIdentifier.Parsed -> return versionedIdentifier
            is VersionedIdentifier.Unparsed -> throw IllegalArgumentException("Cannot resolver version for: ${versionedIdentifier.identifier}")
        }
    }

    interface VersionResolver {
        fun resolve(identifier: VersionedIdentifier.Unparsed): VersionedIdentifier
    }
}