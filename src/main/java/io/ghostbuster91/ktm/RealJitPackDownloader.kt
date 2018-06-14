package io.ghostbuster91.ktm

import io.reactivex.Observable
import java.io.File
import java.net.URL

interface JitPackDownloader {

    fun fetchBuildLog(name: String, version: String, waitingIndicator: Observable<Long>): String

    fun downloadFile(name: String, version: String, file: String, path: File, updateProgress: (Int) -> Unit)

}

class RealJitPackDownloader(logger: Logger) : JitPackDownloader {

    private val okHttpFileDownloader = OkHttpFileDownloader(logger)

    override fun fetchBuildLog(name: String, version: String, waitingIndicator: Observable<Long>): String {
        val waiter = waitingIndicator.subscribe()
        return URL("$jitPackUrl/$name/$version/build.log").readText().also { waiter.dispose() }
    }

    override fun downloadFile(name: String, version: String, file: String, path: File, updateProgress: (Int) -> Unit) {
        okHttpFileDownloader.downloadFile("$jitPackUrl/$name/$version/", path, updateProgress)
    }

    companion object {
        const val jitPackUrl = "https://jitpack.io"
    }
}