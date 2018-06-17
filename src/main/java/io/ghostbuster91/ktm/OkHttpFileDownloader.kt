package io.ghostbuster91.ktm

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

class OkHttpFileDownloader(logger: HttpLoggingInterceptor.Logger, progressListener: () -> ProgressListener) {

    private val client = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.BASIC))
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder().body(ProgressResponseBody(response.body(), progressListener())).build()
            }
            .build()

    fun downloadFile(source: String, destination: File) {
        val response = download(source)
        destination.writeBytes(response.body()!!.bytes())
    }

    private fun download(uri: String): Response {
        val request = Request.Builder().url(uri).build()
        return client.newCall(request).execute()
    }
}