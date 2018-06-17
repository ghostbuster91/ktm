package io.ghostbuster91.ktm

import io.reactivex.Observable
import java.net.URL

interface JitPack {

    fun fetchBuildLog(name: String, version: String): String

    companion object {
        const val jitPackUrl = "https://jitpack.io"
    }
}

class JitPackImpl(private val waitingIndicator: Observable<Long>) : JitPack {

    override fun fetchBuildLog(name: String, version: String): String {
        val waiter = waitingIndicator.subscribe()
        return URL("${JitPack.jitPackUrl}/$name/$version/build.log").readText().also { waiter.dispose() }
    }
}