package io.ghostbuster91.ktm

import io.ghostbuster91.ktm.identifier.Identifier

interface ArtifactToLinkTranslator {
    fun getDownloadLink(identifier: Identifier.Parsed): String


    companion object {
        operator fun invoke(f: (Identifier.Parsed) -> String) =
                object : ArtifactToLinkTranslator {
                    override fun getDownloadLink(identifier: Identifier.Parsed) = f(identifier)
                }
    }
}