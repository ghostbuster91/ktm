package io.ghostbuster91.ktm.identifier

sealed class Identifier {
    data class Parsed(val groupId: String, val artifactId: String) : Identifier() {
        override fun toString() = "$groupId:$artifactId"
    }

    data class Unparsed(val text: String) : Identifier()

}

sealed class VersionedIdentifier {
    abstract val identifier: Identifier.Parsed
    val groupId
        get() = identifier.groupId
    val artifactId
        get() = identifier.artifactId
    val name
        get() = "$groupId:$artifactId"

    data class Parsed(override val identifier: Identifier.Parsed, val version: String) : VersionedIdentifier() {
        val shortVersion = version.take(10)

        override fun toString() = "$identifier:$version"
    }

    data class Unparsed(override val identifier: Identifier.Parsed, val version: String?) : VersionedIdentifier()
}
