package io.ghostbuster91.ktm

import io.reactivex.Observable
import java.net.URL

interface JitPack {

    fun fetchBuildLog(identifier: Identifier): String

    fun getFileUrl(fileName: String): String
}

class JitPackImpl(private val waitingIndicator: Observable<Long>) : JitPack {

    override fun fetchBuildLog(identifier: Identifier): String {
        val waiter = waitingIndicator.subscribe()
        return URL("$jitPackUrl/${identifier.groupId.replace(".", "/")}/${identifier.artifactId}/${identifier.shortVersion}/build.log").readText().also { waiter.dispose() }
    }

    override fun getFileUrl(fileName: String): String {
        return "$jitPackUrl/$fileName"
    }

    companion object {
        private const val jitPackUrl = "https://jitpack.io"
    }
}