package io.ghostbuster91.ktm

import io.reactivex.Observable
import java.io.File
import java.net.URL

interface JitPackDownloader {

    fun fetchBuildLog(name: String, version: String, waitingIndicator: Observable<Long>): String

    fun downloadFile(name: String, version: String, file: String, path: File)

}

class RealJitPackDownloader(logger: Logger, progressListener: () -> ProgressListener) : JitPackDownloader {

    private val okHttpFileDownloader = OkHttpFileDownloader(logger, progressListener)

    override fun fetchBuildLog(name: String, version: String, waitingIndicator: Observable<Long>): String {
        val waiter = waitingIndicator.subscribe()
        return URL("$jitPackUrl/$name/$version/build.log").readText().also { waiter.dispose() }
    }

    override fun downloadFile(name: String, version: String, file: String, path: File) {
        okHttpFileDownloader.downloadFile("$jitPackUrl/$name/$version/$file", path)
    }

    companion object {
        const val jitPackUrl = "https://jitpack.io"
    }
}