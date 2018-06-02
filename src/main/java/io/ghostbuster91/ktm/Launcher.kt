package io.ghostbuster91.ktm

import com.xenomachina.argparser.ArgParser
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Okio
import java.io.File
import java.io.IOException
import java.net.URL

class ParsedArgs(parser: ArgParser) {
    val name by parser.positional("name of the repository")
    val version by parser.positional("version")
}

fun main(args: Array<String>) {
    val logger: (String) -> Unit = { println(it) }
    ArgParser(args)
            .parseInto(::ParsedArgs)
            .run {
                println("Hello, $name! $version")
                doAll(name, version, logger)
            }
}

private fun doAll(name: String, version: String, logger: logger) {
    val homeDir = System.getProperty("user.home")
    val ktmDir = File(homeDir, ".ktm")
    val libDir = File(ktmDir, name.replace("/", "."))
    val versionedLibDir = File(libDir, version)
    if (!versionedLibDir.exists()) {
        versionedLibDir.mkdirs()
    }
    logger(libDir.toString())
    val progressBarUpdater: (Int) -> Unit = { println(it) }
    downloadArtifacts(name, version, logger, versionedLibDir, { a, b -> downloadFile(a, b, progressBarUpdater) })
}

private fun downloadArtifacts(name: String, version: String, logger: logger, versionedLibDir: File, fileDownloader: (String, File) -> Unit) {
    val jitPackUrl = "https://jitpack.io"
    val buildLog = URL("$jitPackUrl/$name/$version/build.log").readText()
    val files = buildLog.substringAfterLast("Files:")
    logger("files : $files")
    files.split("\n")
            .filter { it.isNotBlank() }
            .drop(1)
            .map {
                val source = "$jitPackUrl/$name/$version/$it"
                val destination = File(versionedLibDir, it.substringAfterLast("$version/"))
                source to destination
            }
            .forEach { (source, destination) ->
                println("Processing file $destination")
                fileDownloader(source, destination)
            }
}


private fun downloadFile(source: String, destination: File, progressBarUpdater: (Int) -> Unit) {
    val response = download(source)
    saveResponse(response.body()!!, destination, progressBarUpdater)
}

private fun download(uri: String): Response {
    val client = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    val request = Request.Builder().url(uri).build()
    return client.newCall(request).execute()
}

fun saveResponse(body: ResponseBody, destination: File, progressBarUpdater: progressBarUpdater) {
    val DOWNLOAD_CHUNK_SIZE = 2048L //Same as Okio Segment.SIZE
    try {
        val contentLength = body.contentLength()
        val source = body.source()
        val sink = Okio.buffer(Okio.sink(destination))
        var totalRead: Long = 0
        val buffer = sink.buffer()
        while (!buffer.exhausted()) {
            val read = source.read(buffer, DOWNLOAD_CHUNK_SIZE)
            totalRead += read
            val progress = (totalRead * 100 / contentLength).toInt()
            progressBarUpdater(progress)
        }
        sink.writeAll(source)
        sink.flush()
        sink.close()
    } catch (e: IOException) {
        println(e)
    }
}

typealias logger = (String) -> Unit
typealias progressBarUpdater = (Int) -> Unit
