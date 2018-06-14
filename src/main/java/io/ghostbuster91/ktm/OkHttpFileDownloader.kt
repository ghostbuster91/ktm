package io.ghostbuster91.ktm

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Okio
import java.io.File

class OkHttpFileDownloader(logger: HttpLoggingInterceptor.Logger) {

    private val client = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()

    fun downloadFile(source: String, destination: File, progressUpdate: (Int) -> Unit) {
        val response = download(source)
        saveResponse(response.body()!!, destination, progressUpdate)
    }

    private fun download(uri: String): Response {
        val request = Request.Builder().url(uri).build()
        return client.newCall(request).execute()
    }

    private fun saveResponse(body: ResponseBody, destination: File, progressBarUpdater: (Int) -> Unit) {
        Okio.buffer(Okio.sink(destination)).use { sink ->
            val downloadChunkSize = 2048L //Same as Okio Segment.SIZE
            val source = body.source()
            var totalRead: Long = 0
            val buffer = sink.buffer()
            val contentLength = body.contentLength()
            while (!buffer.exhausted()) {
                val read = source.read(buffer, downloadChunkSize)
                totalRead += read
                val progress = (totalRead * 100 / contentLength).toInt()
                progressBarUpdater(progress)
            }
            sink.writeAll(source)
            sink.flush()
        }
    }
}