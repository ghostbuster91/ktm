package io.ghostbuster91.ktm

import io.ghostbuster91.ktm.identifier.Identifier

interface ArtifactToLinkTranslator {
    fun getDownloadLink(versionedIdentifier: Identifier.Parsed): String


    companion object {
        operator fun invoke(f: (Identifier.Parsed) -> String) =
                object : ArtifactToLinkTranslator {
                    override fun getDownloadLink(versionedIdentifier: Identifier.Parsed) = f(versionedIdentifier)
                }
    }
}