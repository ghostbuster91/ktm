package io.ghostbuster91.ktm

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Okio
import java.io.File
import java.io.IOException

class OkHttpFileDownloader {

    private val client = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    fun downloadFile(source: String, destination: File, progressUpdate: (Int) -> Unit) {
        val response = download(source)
        saveResponse(response.body()!!, destination, progressUpdate)
    }

    private fun download(uri: String): Response {
        val request = Request.Builder().url(uri).build()
        return client.newCall(request).execute()
    }

    private fun saveResponse(body: ResponseBody, destination: File, progressBarUpdater: progressBarUpdater) {
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
}