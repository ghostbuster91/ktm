package io.ghostbuster91.ktm

import io.ghostbuster91.ktm.identifier.VersionedIdentifier

interface ArtifactToLinkTranslator {
    fun getDownloadLink(versionedIdentifier: VersionedIdentifier.Parsed): String


    companion object {
        operator fun invoke(f: (VersionedIdentifier.Parsed) -> String) =
                object : ArtifactToLinkTranslator {
                    override fun getDownloadLink(versionedIdentifier: VersionedIdentifier.Parsed) = f(versionedIdentifier)
                }
    }
}