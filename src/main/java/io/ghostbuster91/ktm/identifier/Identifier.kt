package io.ghostbuster91.ktm.identifier

sealed class Identifier {
    data class Parsed(val groupId: String, val artifactId: String, val version: String) : Identifier() {
        val shortVersion = version.take(10)
        val name = "$groupId:$artifactId"

        override fun toString() = "$groupId:$artifactId:$shortVersion"
    }

    data class Unparsed(val text: String) : Identifier()
}