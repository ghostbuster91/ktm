package io.ghostbuster91.ktm

import com.xenomachina.argparser.ArgParser
import okhttp3.OkHttpClient
import okhttp3.Request
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
    ArgParser(args)
            .parseInto(::ParsedArgs)
            .run {
                println("Hello, $name! $version")
                doAll(name, version)
            }
}

private fun doAll(name: String, version: String) {
    val homeDir = System.getProperty("user.home")

    val ktmDir = File(homeDir, ".ktm")
    val client = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    val libDir = File(ktmDir, name.replace("/", "."))
    val versionedLibDir = File(libDir, version)
    if (!versionedLibDir.exists()) {
        versionedLibDir.mkdirs()
    }
    println(libDir)
    val jitPackUrl = "https://jitpack.io"
    val buildLog = URL("$jitPackUrl/$name/$version/build.log").readText()
    val files = buildLog.substringAfterLast("Files:")
    println("files : $files")
    files.split("\n")
            .filter { it.isNotBlank() }
            .drop(1)
            .forEach {
                println("$it")
                val destination = File(versionedLibDir, it.substringAfterLast("$version/"))
                println("Processing file $destination")
                download("$jitPackUrl/$name/$version/$it", destination, client)
            }
}

val DOWNLOAD_CHUNK_SIZE = 2048L //Same as Okio Segment.SIZE

fun download(uri: String, destination: File, client: OkHttpClient) {
    try {
        val request = Request.Builder().url(uri).build()

        val response = client.newCall(request).execute()
        val body = response.body()
        val contentLength = body!!.contentLength()
        val source = body.source()
        val sink = Okio.buffer(Okio.sink(destination))
        var totalRead: Long = 0
        var read: Long = 0
        val buffer = sink.buffer()
        while (!buffer.exhausted()) {
            read = source.read(buffer, DOWNLOAD_CHUNK_SIZE)
            totalRead += read
            val progress = (totalRead * 100 / contentLength).toInt()
            publishProgress(progress)
        }
        sink.writeAll(source)
        sink.flush()
        sink.close()
    } catch (e: IOException) {
        println(e)
    }

}

fun publishProgress(progress: Int) {
    println(progress)
}
