package io.ghostbuster91.ktm

data class Identifier(val groupId: String, val artifactId: String, val version: String) {

    val shortVersion = version.take(10)
    val name = "$groupId:$artifactId"

    override fun toString() = "$groupId:$artifactId:$shortVersion"

    companion object {
        fun parse(text: String): Identifier {
            val (g, a, v) = text.split(":")
            return Identifier(g, a, v)
        }
    }
}