package io.ghostbuster91.ktm

import java.io.File
import java.net.URL

interface JitPackDownloader {

    fun fetchBuildLog(name: String, version: String): String

    fun downloadFile(name: String, version: String, file:String, path: File, updateProgress: (Int) -> Unit)

}

class RealJitPackDownloader : JitPackDownloader {

    private val okHttpFileDownloader = OkHttpFileDownloader()

    override fun fetchBuildLog(name: String, version: String): String {
        return URL("$jitPackUrl/$name/$version/build.log").readText()
    }

    override fun downloadFile(name: String, version: String, file:String, path: File, updateProgress: (Int) -> Unit) {
        okHttpFileDownloader.downloadFile("$jitPackUrl/$name/$version/", path, updateProgress)
    }

    companion object {
        const val jitPackUrl = "https://jitpack.io"
    }
}