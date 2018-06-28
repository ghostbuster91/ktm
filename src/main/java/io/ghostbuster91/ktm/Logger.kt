package io.ghostbuster91.ktm

import okhttp3.logging.HttpLoggingInterceptor

interface Logger : HttpLoggingInterceptor.Logger {
    fun error(msg: String, e: Throwable)
    override fun log(msg: String)
    fun append(msg: String)
    fun info(msg: Any?)
}