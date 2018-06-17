package io.ghostbuster91.ktm

import io.reactivex.Observable
import java.net.URL

interface JitPack {

    fun fetchBuildLog(name: String, version: String): String

    fun getFileUrl(fileName: String) : String
}

class JitPackImpl(private val waitingIndicator: Observable<Long>) : JitPack {

    override fun fetchBuildLog(name: String, version: String): String {
        val waiter = waitingIndicator.subscribe()
        return URL("$jitPackUrl/$name/$version/build.log").readText().also { waiter.dispose() }
    }

    override fun getFileUrl(fileName: String): String {
        return "$jitPackUrl/$fileName"
    }

    companion object {
        private const val jitPackUrl = "https://jitpack.io"
    }
}