package io.ghostbuster91.ktm.identifier.version

import io.ghostbuster91.ktm.utils.foldUntil

class VersionSolverDispatcher(private val versionResolver: List<VersionResolver>) {
    fun resolve(identifier: VersionedIdentifier): VersionedIdentifier.Parsed {
        val versionedIdentifier = versionResolver.foldUntil(identifier, { acc, resolver -> resolver.resolve(acc as VersionedIdentifier.Unparsed) }, { acc -> acc is VersionedIdentifier.Unparsed })
        when (versionedIdentifier) {
            is VersionedIdentifier.Parsed -> return versionedIdentifier
            is VersionedIdentifier.Unparsed -> throw VersionUnresolved("Cannot resolver version for: $versionedIdentifier \n" +
                    "Note that automatic version pickup works only for artifacts which have remote tags.\n" +
                    "For other artifacts provide version explicitly using --version option.")
        }
    }

    interface VersionResolver {
        fun resolve(identifier: VersionedIdentifier.Unparsed): VersionedIdentifier
    }

    sealed class VersionedIdentifier {
        abstract val groupId: String
        abstract val artifactId: String

        data class Parsed(override val groupId: String, override val artifactId: String, val version: String) : VersionedIdentifier() {
            override fun toString() = "$groupId:$artifactId:$version"
        }

        data class Unparsed(override val groupId: String, override val artifactId: String, val version: String?) : VersionedIdentifier() {
            override fun toString() = "$groupId:$artifactId"
            fun toParsed(version: String) = Parsed(groupId, artifactId, version)
        }
    }

    class VersionUnresolved(override val message: String) : RuntimeException(message)
}