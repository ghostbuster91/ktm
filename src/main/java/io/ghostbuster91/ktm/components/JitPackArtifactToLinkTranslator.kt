package io.ghostbuster91.ktm.components

import io.ghostbuster91.ktm.ArtifactToLinkTranslator
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.logger
import io.reactivex.Observable
import java.net.URL

class JitPackArtifactToLinkTranslator(private val waitingIndicator: Observable<out Any>) : ArtifactToLinkTranslator {

    override fun getDownloadLink(identifier: Identifier.Parsed): String {
        logger.append("Fetching build log from JitPack...")
        val buildLog = fetchBuildLog(identifier)
        val files = buildLog.substringAfterLast("Files:").split("\n").filter { it.isNotBlank() }.drop(1)
        require(files.isNotEmpty(), { "Didn't find any artifacts!" })
        val filesFQN = files.map { getFileUrl(it) }
        return filesFQN.findArchive()
    }

    private fun List<String>.findArchive(): String {
        logger.info("Found $size files:")
        forEach(logger::info)
        val archive = firstOrNull { it.substringAfterLast(".") == "tar" }
        require(archive != null, { "No tar archives found!" })
        return archive!!
    }

    private fun fetchBuildLog(identifier: Identifier.Parsed): String {
        val waiter = waitingIndicator.subscribe()
        return URL("$jitPackUrl/${identifier.groupId.replace(".", "/")}/${identifier.artifactId}/${identifier.shortVersion}/build.log").readText().also { waiter.dispose() }
    }

    private fun getFileUrl(fileName: String): String {
        return "$jitPackUrl/$fileName"
    }

    companion object {
        private const val jitPackUrl = "https://jitpack.io"
    }
}